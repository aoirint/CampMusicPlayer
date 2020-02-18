package com.github.aoirint.campmusicplayer.util;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;

import java.io.IOException;
import java.nio.ByteBuffer;

import static com.github.aoirint.campmusicplayer.activity.main.MainActivity.logger;

public class MusicUtil {

    public static AudioTrack playKaraoke(Context context, Uri uri) throws IOException {
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();

        MediaExtractor extractor = new MediaExtractor();
        extractor.setDataSource(context, uri, null);

        // find first track
        MediaCodec decoder = null;
        int numTracks = extractor.getTrackCount();
        logger.info("tracks: " + numTracks);
        for (int trackIndex=0; trackIndex<numTracks; trackIndex++) { // assume each audio has only one track
            MediaFormat format = extractor.getTrackFormat(trackIndex);
            String mimetype = format.getString(MediaFormat.KEY_MIME);

            if (mimetype.startsWith("audio/")) {
                extractor.selectTrack(trackIndex);
                decoder = MediaCodec.createDecoderByType(mimetype);
                decoder.configure(format, null, null, 0);
                break;
            }
        }
        if (decoder == null) throw new IllegalArgumentException("No decoder for the file format.");
        decoder.start();

        MediaFormat format = decoder.getOutputFormat();
        int channelCount = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);

        if (channelCount != 2) throw new IllegalArgumentException("Stereo audio is required.");

        int pcmEncoding = format.getInteger(MediaFormat.KEY_PCM_ENCODING);
        int sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
        int chanelConfig = AudioFormat.CHANNEL_OUT_STEREO;

        int bufferSize = AudioTrack.getMinBufferSize(sampleRate, chanelConfig, pcmEncoding);

        AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, chanelConfig, pcmEncoding, bufferSize, AudioTrack.MODE_STREAM);
        track.play();

        while (true) {
            int inputBufferIndex = decoder.dequeueInputBuffer(10000);
            ByteBuffer inputBuffer = decoder.getInputBuffer(inputBufferIndex);

            int size = extractor.readSampleData(inputBuffer, 0);
            if (size < 0) {
                decoder.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
            }
            else {
                decoder.queueInputBuffer(inputBufferIndex, 0, size, extractor.getSampleTime(), 0);
                extractor.advance();
            }

            if (info.flags != 0) break; // EOF

            int outputBufferIndex = MediaCodec.INFO_TRY_AGAIN_LATER;
            while (outputBufferIndex < 0) {
                outputBufferIndex = decoder.dequeueOutputBuffer(info, 10000);
            }

            ByteBuffer outputBuffer = decoder.getOutputBuffer(outputBufferIndex);

            outputBuffer.rewind();

            int byteCount = outputBuffer.remaining();
            byte[] outBytes = new byte[byteCount];
            outputBuffer.get(outBytes);

            int sampleSize = 2; // 16 bits
            int waveMax = (0b1 << (8*sampleSize-1)) - 1;
            int waveMin = -waveMax - 1;
            for (int i=0; i<byteCount/4; i++) {
                int waveLeft = (outBytes[i*4] << 8) | outBytes[i*4 + 1];
                int waveRight = (outBytes[i*4 + 2] << 8) | outBytes[i*4 + 3];

                int sub = waveLeft - waveRight;
                int wave = Math.min(Math.max(waveMin, sub), waveMax);

                byte[] value = ByteBuffer.allocate(2).putShort((short) wave).array();

                outBytes[i*4] = value[0];
                outBytes[i*4 + 1] = value[1];
                outBytes[i*4 + 2] = value[0];
                outBytes[i*4 + 3] = value[1];
            }

            track.write(outBytes, info.offset, info.size);

            decoder.releaseOutputBuffer(outputBufferIndex, false);
        }

        decoder.stop();
        decoder.release();

        return track;
    }

}
