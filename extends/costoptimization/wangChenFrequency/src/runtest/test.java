package runtest;

/**
 * Created by lenovo on 2017/7/8.
 */
public class test {
    private void test(boolean flag){
        flag=true;
    }
    public static void main(String[] args){
        boolean flag=false;
        test t=new test();
        t.test(flag);
        System.out.print(flag);
    }
}
