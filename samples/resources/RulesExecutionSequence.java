/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. All Rights Reserved.
 */

package samples.drools;

import org.adroitlogic.ultraesb.api.Configuration;
import org.adroitlogic.ultraesb.api.JavaClassSequence;
import org.adroitlogic.ultraesb.api.Mediation;
import org.adroitlogic.ultraesb.api.Message;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.compiler.DroolsError;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderErrors;
import org.adroitlogic.logging.api.Logger;
import org.adroitlogic.logging.api.LoggerFactory;

import java.io.*;

/**
 * A sample sequence to initialize the rules engine
 * @author amindri udugala
 */
public class RulesExecutionSequence implements JavaClassSequence {

    private static final Logger logger = LoggerFactory.getLogger("sequence");
    private static final String DRL_FILE = "samples/resources/rules.drl";
    private static RuleBase rbase = RuleBaseFactory.newRuleBase();
    private StatefulSession sessionObject;


    @Override
    public void init(Configuration config) {
        logger.info("Started the rules execution sequence");
        PackageBuilder pbuilder = new PackageBuilder();

        try {
            Reader reader = new InputStreamReader(new FileInputStream(DRL_FILE));
            pbuilder.addPackageFromDrl(reader);
        } catch (IOException e) {
            logger.error("Error in reading the DRL file : {}", e);
        } catch (DroolsParserException e) {
            logger.error("Error in parsing the DRL file : {}", e);
        }

        // Check for any errors
        PackageBuilderErrors errors = pbuilder.getErrors();

        if (errors.getErrors().length > 0) {
            logger.error("Some errors exists in packageBuilder");
            DroolsError pbErrors[] = errors.getErrors();
            for (DroolsError error : pbErrors) {
                logger.error("Error : {}", error);
            }
        }

        // Add package to rule base
        rbase.addPackage(pbuilder.getPackage());
        sessionObject = rbase.newStatefulSession();
    }

    @Override
    public void destroy() {
        sessionObject.dispose();
        logger.info("Stopped the rules execution sequence");
    }

    @Override
    public void execute(Message msg, Mediation mediation) throws Exception {

        //insert the helper object
        LoanApplication application = mediation.getJSONSupport().convertToTypedJSON(msg, LoanApplication.class);
        sessionObject.insert(application);

        //fire the rules
        sessionObject.fireAllRules();
        mediation.getJSONSupport().convertJSONToStream(msg, application);

        String response;
        if (application.isLoanApproved()) {
            response = "Your loan application is approved. The interest rate is, " + application.getInterestRate() + "%";
            mediation.setPayloadFromString(msg, response);
            mediation.sendResponse(msg, 200);
        } else {
            response = "Your loan application is rejected";
            mediation.setPayloadFromString(msg, response);
            mediation.sendResponse(msg, 200);
        }
    }
}
