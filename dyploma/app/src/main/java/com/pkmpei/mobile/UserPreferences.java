package com.pkmpei.mobile;

/**
 * Created by Harry on 22.05.2016.
 */
public class UserPreferences {

    private String FIO;
    private String birthDate;
    private String imei;
    private String phpSessId;

    private static UserPreferences instance;
    private UserPreferences() {
        FIO = "";
        birthDate = "";
        imei = "";
        phpSessId = "";
    }
    public static UserPreferences getInstance() {
        if (instance == null) {
            instance = new UserPreferences();
        }
        return instance;
    }

    public String getPhpSessId() {
        return phpSessId;
    }

    public void setPhpSessId(String phpSessId) {
        this.phpSessId = phpSessId;
    }

    public String getFIO() {
        return FIO;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setFIO(String FIO) {
        this.FIO = FIO;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}
