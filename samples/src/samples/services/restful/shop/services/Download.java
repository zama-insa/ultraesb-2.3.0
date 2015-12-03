/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package samples.services.restful.shop.services;

import org.adroitlogic.ultraesb.api.transport.BaseConstants;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Path("/download")
/**
 * Class to be used for manual testing with large content-length encoded payloads
 */
public class Download {

    @GET
    public Response downloadFile(@QueryParam("token")  @Encoded String token,
                                 @QueryParam("file")   @Encoded String file,
                                 @HeaderParam("Accept-Encoding") String encoding) {

        final String dummyFile = "/storage/NonBackup/Music/Audio/Flat/Words.mp3"; /*Make sure there is a couple meg file here*/
        ResponseBuilder builder;

        builder = Response.ok(new StreamingOutput() {

            public void write(OutputStream output)
                throws IOException, WebApplicationException {

                FileChannel fc = new FileInputStream(dummyFile).getChannel();
                ByteBuffer bb = ByteBuffer.allocateDirect(16*1024);

                while(fc.read(bb) != -1) {
                    bb.flip();

                    while(bb.hasRemaining()) {
                        output.write(bb.get());
                    }

                    bb.compact();
                }
                fc.close();
            }
        });

        builder.header("Content-Disposition", "attachment; filename=\"Words.mp3\"");
        builder.header(BaseConstants.Headers.CONTENT_TYPE, "application/octet-stream");

        return builder.build();
    }
}

