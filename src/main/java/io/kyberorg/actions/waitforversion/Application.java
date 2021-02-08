package io.kyberorg.actions.waitforversion;

import kong.unirest.GetRequest;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONException;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;

public class Application  {
    private static String url;
    private static int responseCode;
    private static float timeout;
    private static float interval;
    private static boolean hasActuator;
    private static String commitSha;

    private static boolean versionCheck;

    private static final String EMPTY_STRING_FLAG = "EmptyString";
    private static final int MILLISECONDS_IN_SECOND = 1000;

    public static void main(String[] args) {
        if (args.length < 5) {
            System.err.println("Usage: wait4version <url> <responseCode> <timeout> <interval> <commitSha>");
            System.exit(3);
        }
        checkArgs(args);


        versionCheck = !commitSha.equals(EMPTY_STRING_FLAG);
        if(versionCheck) {
            System.out.printf("Polling URL '%s',waiting for response code %s and new version with commit SHA %s %n",
                    url, responseCode, commitSha);
        } else {
            System.out.printf("Polling URL '%s' and waiting for response code %s %n", url, responseCode);
        }

        long timeoutInMilliSeconds = (long) timeout * MILLISECONDS_IN_SECOND;

        Instant execTimeout = Instant.now().plusMillis(timeoutInMilliSeconds);

        do {
            doCheck();
            doPause();
        } while (Instant.now().isBefore(execTimeout));
        //Timeout reached
        System.err.println("All checks failed. Condition unmet. Timeout reached");
        System.exit(1);
    }

    private static void doCheck() {
        String endpoint;

        if(hasActuator) {
            if(versionCheck) {
                endpoint = url + "/actuator/info";
            } else {
                endpoint = url + "actuator/health";
            }
        } else {
            endpoint = url;
        }
        GetRequest request = Unirest.get(endpoint);
        if(request.asEmpty().getStatus() == responseCode) {
            if (hasActuator) {
                HttpResponse<JsonNode> responseFromActuator = request.asJson();
                if(versionCheck) {
                    try {
                      String appCommitSha = responseFromActuator.getBody()
                              .getObject().getJSONObject("git").getJSONObject("commit").getString("id");
                      if(appCommitSha.equals(commitSha)) {
                          System.out.printf("Check succeeded. Status: %s. Version: %s found. Condition met %n",
                                  request.asEmpty().getStatus(), appCommitSha);
                          System.exit(0);
                      }
                    } catch (JSONException e) {
                        System.err.println(e.getMessage());
                    }
                } else {
                    try {
                        String status = responseFromActuator.getBody().getObject().getString("status");
                        if(status.equals("UP")) {
                            System.out.printf("Check succeeded. Status: %s. Application is UP. Condition met %n",
                                    request.asEmpty().getStatus());
                            System.exit(0);
                        }
                    } catch (JSONException e) {
                        System.err.println(e.getMessage());
                    }

                }
            } else {
                //all good - report success
                System.out.printf("Check succeeded. Status: %s. Application is UP. Condition met %n",
                        request.asEmpty().getStatus());
                System.exit(0);
            }
        }
        System.out.printf("Check failed. Status: %s. Condition unmet %n", request.asEmpty().getStatus());
    }

    private static void doPause() {
        // do pause
        System.out.printf("Pausing for %f seconds %n", interval);
        long pauseInMillis = (long) interval * MILLISECONDS_IN_SECOND;
        try {
            Thread.sleep(pauseInMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void checkArgs(String[] args) {
        url = args[0];
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            System.err.printf("'%s' is not valid URL", url);
            System.exit(3);
        }

        try {
            responseCode = Integer.parseInt(args[1]);
            if ((responseCode < 200) || (responseCode > 599)) {
                System.err.printf("'%s' is not valid response code. Should be from 200 to 599", args[1]);
                System.exit(3);
            }
        } catch (NumberFormatException e) {
            System.err.printf("'%s' is not valid response code. Should be integer from 200 to 599", args[1]);
            System.exit(3);
        }

        try {
            timeout = Float.parseFloat(args[2]);
            if (timeout < 0 ) {
                System.err.printf("'%s' is not valid timeout. Should be non negative integer or float.", args[2]);
                System.exit(3);
            }
        } catch (NumberFormatException e) {
            System.err.printf("'%s' is not valid timeout. Should be non negative integer or float.", args[2]);
            System.exit(3);
        }

        try {
            interval = Float.parseFloat(args[3]);
            if (interval < 0 ) {
                System.err.printf("'%s' is not valid interval. Should be non negative integer or float.", args[3]);
                System.exit(3);
            }
        } catch (NumberFormatException e) {
            System.err.printf("'%s' is not valid interval. Should be non negative integer or float.", args[3]);
            System.exit(3);
        }

        hasActuator = Boolean.parseBoolean(args[4]);

        commitSha = args[5];
        if (commitSha == null || commitSha.trim().isBlank()) {
           commitSha = EMPTY_STRING_FLAG;
        }
    }
}
