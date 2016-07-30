/**
 * Created by sags on 3/15/16.
 */

// Distance class to store infinity

public class Distance
{

    private long distance;
    private boolean isInfinity;

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance)
    {
        if(!this.isInfinity)
        {
        this.distance = distance;
        }
        else
        {

            System.err.println("Check assignment for distance");

        }
    }

    public boolean isInfinity() {
        return isInfinity;
    }

    public void setInfinity(boolean isInfinity)
    {

        this.isInfinity = isInfinity;
    }


}
