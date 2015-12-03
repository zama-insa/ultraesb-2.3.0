/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.core;

import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.HessianInput;
import org.adroitlogic.ultraesb.api.JavaClassSequence;
import org.adroitlogic.ultraesb.api.Message;
import org.adroitlogic.ultraesb.api.Mediation;
import org.adroitlogic.ultraesb.api.Configuration;
import samples.services.soap.GQ;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author asankha
 */
@SuppressWarnings({"UnusedDeclaration"})
public class SampleHessianSequence implements JavaClassSequence {

    @SuppressWarnings({"UnusedAssignment"})
    public void execute(Message msg, Mediation mediation) throws Exception {

        InputStream is = msg.getCurrentPayload().getInputStream();

        int code = is.read();
        int major;
        int minor;

        AbstractHessianInput in;

        if (code == 'H') {
            major = is.read();
            minor = is.read();

            if (major != 0x02 || minor != 0x00) {
                throw new IOException("Version " + major + "." + minor + " is not understood");
            }

            in = new Hessian2Input(is);
            in.readCall();

        } else if (code == 'c') {
            major = is.read();
            minor = is.read();

            in = new HessianInput(is);

        } else {
            // XXX: deflate
            throw new IOException("expected 'H' (Hessian 2.0) or 'c' (Hessian 1.0) in hessian input at " + code);
        }

        // backward compatibility for some frameworks that don't read
        // the call type first
        in.skipOptionalCall();

        // Hessian 1.0 backward compatibility
        String header;
        do {
            header = in.readHeader();
            // forget these
            // Object value = in.readObject();
            // context.addHeader(header, value);
        } while (header != null);

        String methodName = in.readMethod();
        Object obj = in.readObject(null);
        if (obj instanceof GQ) {
            GQ gq = (GQ) obj;
            System.out.println("Request is for symbol : " + gq.getSymbol());
        }
        is.close();
    }

    public void init(Configuration config) {
    }

    public void destroy() {
    }
}
