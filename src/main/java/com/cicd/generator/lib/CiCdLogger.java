package com.cicd.generator.lib;

public interface CiCdLogger {
    void info(String message);
    void debug(String message);
    void error(String message, Exception exception);

    // Default implementation using System.out
    static CiCdLogger console() {
        return new CiCdLogger() {
            @Override
            public void info(String message) {
                System.out.println("[INFO] " + message);
            }

            @Override
            public void debug(String message) {
                System.out.println("[DEBUG] " + message);
            }

            @Override
            public void error(String message, Exception exception) {
                System.err.println("[ERROR] " + message);
                if (exception != null) {
                    exception.printStackTrace();
                }
            }
        };
    }
}