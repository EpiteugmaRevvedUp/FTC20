package org.firstinspires.ftc.teamcode;



import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import java.lang.Runnable;
import java.util.Locale;


@TeleOp(name="Basic: Iterative OpMode", group="FTC_Cyprus_2020-21")



public class drive extends LinearOpMode {



    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor fl = null;
    private DcMotor bl = null;
    private DcMotor br = null;
    private DcMotor fr = null;
    private double sidepowerfactor = 0.7;
    private double forwardpowerfactor = 0.6;
    private double turnpowerfactor = 0.47;
    BNO055IMU imu;
    Orientation angles;
    Acceleration gravity;
    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void runOpMode() {
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);
        telemetry.addData("Status", "Initialized");
        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        fl = hardwareMap.get(DcMotor.class, "front_left");
        fr = hardwareMap.get(DcMotor.class, "front_right");
        bl = hardwareMap.get(DcMotor.class, "back_left");
        br = hardwareMap.get(DcMotor.class, "back_right");
        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
//        leftDrive.setDirection(DcMotor.Direction.FORWARD);
//        rightDrive.setDirection(DcMotor.Direction.REVERSE);

        // Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized");

        waitForStart();
        Thread drivethread = new Thread(driver);
        Thread imuthread = new Thread(imuRead);
        imuthread.start();
        drivethread.start();
        while (opModeIsActive()) {

        }

        /*
         * Code to run ONCE after the driver hits STOP
         */

    }

    Runnable driver = new Runnable() {
        @Override
        public void run() {
            while (opModeIsActive()) {
                // Read controller variables
                double forwardpower = -gamepad1.left_stick_y * forwardpowerfactor;
                double sidepower = gamepad1.left_stick_x * sidepowerfactor;
                double turnpower = gamepad1.right_stick_x * turnpowerfactor * -1;

                // Calculate DC Motor Power.
                fl.setPower((forwardpower + sidepower + turnpower));
                bl.setPower((forwardpower - sidepower + turnpower));
                fr.setPower(-(forwardpower - sidepower - turnpower));
                br.setPower(-(forwardpower + sidepower - turnpower));
            }
        }


    };

    Runnable imuRead = new Runnable() {
        @Override
        public void run() {
            while (opModeIsActive()) {
                angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
                telemetry.addLine() .addData("Z Coordinate (Rotation)", formatAngle(AngleUnit.DEGREES, angles.firstAngle));
                telemetry.update();
            }
        }
    };

    Runnable factorGearbox = new Runnable() {
        @Override
        public void run() {
            while (opModeIsActive()) {
                while(gamepad1.left_stick_y == 1 || gamepad1.left_stick_x == 1) {
                    if(forwardpowerfactor != 1) {
                        forwardpowerfactor += 0.1;
                    }
                    try{Thread.sleep(500);}
                    catch (Exception e) {}
                }
                while(gamepad1.left_stick_y == -1 || gamepad1.left_stick_y == -1) {
                    if(forwardpowerfactor != 0) {
                        forwardpowerfactor -= 0.1;
                    }
                    try{Thread.sleep(500);}
                    catch (Exception e) {}
                }
            }
        }
    };
    
    /////////////////////////////////////
    //            FORMATTING           //
    /////////////////////////////////////

    String formatAngle(AngleUnit angleUnit, double angle) {
        return formatDegrees(AngleUnit.DEGREES.fromUnit(angleUnit, angle));
    }

    String formatDegrees(double degrees){
        return String.format(Locale.getDefault(), "%.1f", AngleUnit.DEGREES.normalize(degrees));
    }
}

