package tysheng.gank.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shengtianyang on 16/3/26.
 */
public class GankCategory implements Serializable {

    /**
     * error : false
     */

    public boolean error = false;
    /**
     * _id : 56f4a65367765933dbbd20e6
     * _ns : ganhuo
     * createdAt : 2016-03-25T10:45:39.651Z
     * desc :  materialdoc 教程
     * publishedAt : 2016-03-25T11:23:49.570Z
     * source : chrome
     * type : Android
     * url : https://github.com/materialdoc/materialdoc
     * used : true
     * who : 花开堪折枝
     */

    public List<GankResult> results = new ArrayList<>();

}
