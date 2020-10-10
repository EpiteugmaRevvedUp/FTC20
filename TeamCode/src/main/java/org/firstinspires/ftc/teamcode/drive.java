package org.firstinspires.ftc.teamcode;



import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import java.lang.Runnable;



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
    private double turnpowerfactor = 0.7;


    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        fl = hardwareMap.get(DcMotor.class, "front_left");
        fr = hardwareMap.get(DcMotor.class, "front_right");
        bl = hardwareMap.get(DcMotor.class, "back_left");
        br = hardwareMap.get(DcMotor.class, "back_right");

        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
//        leftDrive.setDirection(DcMotor.Direction.FORWARD);
//        rightDrive.setDirection(DcMotor.Direction.REVERSE);

        // Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized");

        waitForStart();
        Thread drivethread = new Thread(driver);
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
                double turnpower = gamepad1.right_stick_x * turnpowerfactor;

                // Calculate DC Motor Power.
                fl.setPower((forwardpower + sidepower + turnpower));
                bl.setPower((forwardpower - sidepower + turnpower));
                fr.setPower(-(forwardpower - sidepower - turnpower));
                br.setPower(-(forwardpower + sidepower - turnpower));
            }
        }


    };
}
