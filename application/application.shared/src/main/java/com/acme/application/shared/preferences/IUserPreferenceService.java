package com.acme.application.shared.preferences;

import java.util.Properties;

import org.eclipse.scout.rt.shared.TunnelToServer;

@TunnelToServer
public interface IUserPreferenceService {

    Properties load(String userId, String node);

    void store(String userId, String node, Properties prefs);

}