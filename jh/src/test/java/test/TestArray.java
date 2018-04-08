package test;

import com.google.gson.Gson;
import org.junit.Test;

public class TestArray {

    private int[] array = {1,2,3,4,5,6,7,8,9,10,1,16,4,29,2};

    @Test
    public void testSort() {

        int min = array[0];

        int temp = array[0];

        int max = array[0];

        ResultBean resultBean = new ResultBean(min,max);

        for(int i=0;i<array.length;i++) {
            if(i==0) {
                continue;
            }

            if(checkNodeIsPeek(i)) {
                if((array[i] - min) > resultBean.getRange()) {
                    max = array[i];
                    resultBean = new ResultBean(min,array[i]);
                } else {
                    continue;
                }
            }

            if(checkNodeIsBottom(i)) {
                min = array[i];
                max = array[i];
            }
        }

        System.out.println(new Gson().toJson(resultBean));
    }

    private boolean checkNodeIsPeek(int i) {
        if(array.length-1 == i) {
            return array[i] > array[i-1];
        } else {
            return array[i] >= array[i - 1] && array[i] >= array[i + 1];
        }
    }

    private boolean checkNodeIsBottom(int i) {
        if(i==array.length-1) {
            return array[i]<=array[i-1];
        } else {
            return array[i]<=array[i-1] && array[i]<=array[i+1];
        }

    }
}

class ResultBean {
    private int from;
    private int to;
    private int range;

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public ResultBean(int from,int to) {
        this.from = from;
        this.to = to;
        this.range = to-from;
    }
}
