package tetracoveragearea.common.telnet.bsFeatures.multiFeatures;

import java.util.Arrays;

/**
 * Created by anatoliy on 18.05.17.
 */
public class ShortData_Feature extends MultiFeature {

    private boolean locationPDU = false;
    private double latitude;
    private double longitude;
    private int directionAngle;
    private int velocity;
    private int positionError;

    UserDefinedData_Feature userDefinedData_feature = new UserDefinedData_Feature();

    public ShortData_Feature() {
        setFeature(Arrays.asList(userDefinedData_feature));
    }

    @Override
    public int getLength() {
        return 4;
    }

    @Override
    public void parse() {
        super.parse();
        byte[] bytes = userDefinedData_feature.getStream().getFeature();
        if (bytes[0] == 0x0a && (bytes[1] & 0xc0) == 0) {

            locationPDU = true;
            /** Преобразование битов в навигационные данные по стандарту */
            latitude =
                    (((bytes[4] & 0x07) << 21)
                            + ((bytes[5] & 0xff) << 13)
                            + ((bytes[6] & 0xff) << 5)
                            + ((bytes[7] & 0xf8) >> 3)
                    ) / Math.pow(2, 24) * 180;

            longitude =
                    (((bytes[1] & 0x0f) << 21)
                            + ((bytes[2] & 0xff) << 13)
                            + ((bytes[3] & 0xff) << 5)
                            + ((bytes[4] & 0xf8) >> 3)
                    ) / Math.pow(2, 25) * 360;

            directionAngle = (int) ((360 / 16.0) * (((bytes[8] & 0x01) << 3)
                    + ((bytes[9] & 0xe0) >> 5)));

            velocity = (int) (16*Math.pow(1.038,(bytes[8] & 0xfe >> 1) - 13));

            positionError = 2 * (int) Math.pow(10, bytes[7] & 0x07);
        }
    }

    public boolean isLocationPDU() {
        return locationPDU;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getDirectionAngle() {
        return directionAngle;
    }

    public int getVelocity() {
        return velocity;
    }

    public int getPositionError() {
        return positionError;
    }

    public UserDefinedData_Feature getUserDefinedData_feature() {
        return userDefinedData_feature;
    }
}
