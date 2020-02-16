package com.github.aoirint.campmusicplayer.util;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;

import com.github.aoirint.campmusicplayer.activity.main.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {

    public static String calcUriStringHash(Uri uri) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("md5"); // fast calc
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        md.update(uri.toString().getBytes(StandardCharsets.UTF_8));

        return new String(md.digest(), StandardCharsets.US_ASCII);
    }

    public static String calcFileHash(Context context, Uri uri) throws IOException {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("md5"); // fast calc
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        InputStream is = context.getContentResolver().openInputStream(uri);
        DigestInputStream dis = new DigestInputStream(is, md);

        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        while (dis.read(buffer) != -1); // read to EOF
        dis.close();

        return new String(md.digest(), StandardCharsets.US_ASCII);
    }

    @Deprecated
    public static String calcAudioHeadHash(Context context, Uri uri) throws IOException {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("md5"); // fast calc
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();

        MediaExtractor extractor = new MediaExtractor();
        extractor.setDataSource(context, uri, null);

        // find first track
        MediaCodec decoder = null;
        int numTracks = extractor.getTrackCount();
        MainActivity.logger.info("tracks: " + numTracks);
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
        if (decoder == null) throw new IllegalArgumentException("No decoder for the file format");
        decoder.start();

        final int headSize = 44100 * 3;
        int sizeRead = 0;
        byte[] buffer = new byte[headSize];
        while (sizeRead < headSize) {
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

            int dataEnd = sizeRead + size;
            if (headSize < dataEnd) dataEnd = headSize;
            int dataLen = dataEnd - sizeRead;

            outputBuffer.rewind();

            int remaining = outputBuffer.remaining();
            byte[] outBytes = new byte[remaining];
            outputBuffer.get(outBytes);

            System.arraycopy(outBytes, 0, buffer, sizeRead, dataLen); // [:headSize]

            decoder.releaseOutputBuffer(outputBufferIndex, false);
            sizeRead += size;
        }
        md.update(buffer); // use 44100 * 3 bytes (head)

        decoder.stop();
        decoder.release();

        String hash = new String(md.digest(), "ascii");
        return hash;
    }

}
