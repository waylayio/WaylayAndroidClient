package waylay.client.statistcs;
import android.hardware.SensorManager;

public class SimpleLinearAcceleration
{

       // The gravity components of the acceleration signal.
       private float[] components = new float[3];

       private float[] linearAcceleration = new float[]
       { 0, 0, 0 };

       // Raw accelerometer data
       private float[] acceleration = new float[]
       { 0, 0, 0 };

       private LowPassFilter lpfAcceleration;

       private MeanFilter meanFilterAcceleration;

       private StdDev varianceAccel;


       public SimpleLinearAcceleration(
                       LowPassFilter lpfAcceleration, 
                       MeanFilter meanFilterAcceleration)
       {
               super();
               this.lpfAcceleration = lpfAcceleration;
               this.meanFilterAcceleration = meanFilterAcceleration;

               // Create the RMS Noise calculations
               varianceAccel = new StdDev();
       }
       
       public SimpleLinearAcceleration()
		{
		       this(null, null);
		}

       /**
        * Add a sample.
        * 
        * @param acceleration
        *            The acceleration data.
        * @return Returns the output of the filter.
        */
       public float[] addSamples(float[] acceleration)
       {
               // Get a local copy of the sensor values
               System.arraycopy(acceleration, 0, this.acceleration, 0,
                               acceleration.length);
               
               if (lpfAcceleration != null)
               {
                       System.arraycopy(lpfAcceleration.addSamples(this.acceleration), 0,
                                       this.acceleration, 0, this.acceleration.length);
               }

               if (meanFilterAcceleration != null)
               {
                       this.acceleration = meanFilterAcceleration
                                       .filterFloat(this.acceleration);
               }

               float magnitude = (float) (Math.sqrt(Math.pow(this.acceleration[0], 2)
                               + Math.pow(this.acceleration[1], 2)
                               + Math.pow(this.acceleration[2], 2)) / SensorManager.GRAVITY_EARTH);

               double var = varianceAccel.addSample(magnitude);

               // Attempt to estimate the gravity components when the device is
               // stable and not experiencing linear acceleration.
               if (var < 0.05)
               {
                       // Find the gravity component of the X-axis
                       components[0] = this.acceleration[0];
                       // Find the gravity component of the Y-axis
                       components[1] = this.acceleration[1];
                       // Find the gravity component of the Z-axis
                       components[2] = this.acceleration[2];
               }

               // Subtract the gravity component of the signal
               // from the input acceleration signal to get the
               // tilt compensated output.
               linearAcceleration[0] = (this.acceleration[0] - components[0]);
               linearAcceleration[1] = (this.acceleration[1] - components[1]);
               linearAcceleration[2] = (this.acceleration[2] - components[2]);

               return linearAcceleration;
       }

}