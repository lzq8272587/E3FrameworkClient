package framework.mobisys.netlab.framework;

import java.util.HashMap;

/**
 * Created by LZQ on 11/25/2015.
 */
public class Request implements Comparable<Request> {
    String url=null;

    int delay;

    String tag=null;

    HashMap<String, String> extraHeader=null;

    int Method=-1;

    Request(String url, int delay,String tag)
    {
        this.url=url;
        this.delay=delay;
        this.tag=tag;
    }

    public void setExtraHeader(HashMap<String, String> header)
    {
        extraHeader=header;
    }

    public void setMethod(int method)
    {
        this.Method=method;
    }


    /**
     * Compares this object to the specified object to determine their relative
     * order.
     *
     * @param another the object to compare to this instance.
     * @return a negative integer if this instance is less than {@code another};
     * a positive integer if this instance is greater than
     * {@code another}; 0 if this instance has the same order as
     * {@code another}.
     * @throws ClassCastException if {@code another} cannot be converted into something
     *                            comparable to {@code this} instance.
     */
    @Override
    public int compareTo(Request another) {
        return 0;
    }
}
