//package state;
//
//import org.junit.Assert;
//import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * @Author Gq
// * @Date 2021/2/3 17:42
// * @Version 1.0
// **/
//public class TestSimState {
//    private final Logger logger = LoggerFactory.getLogger(TestSimState.class);
//
//    @Test
//    public void testClone(){
//        SimState origin = new SimState();
//        origin.collect("s1", new float[]{1,1,1}, new float[]{1,1,1}, new float[]{1,1,1});
//        origin.collect("s2", new float[]{2,2,2}, new float[]{2,2,2}, new float[]{2,2,2});
//        origin.collect("s3", new float[]{3,3,3}, new float[]{3,3,3}, new float[]{3,3,3});
//
//        SimState copy = new SimState(origin);
//        Assert.assertEquals(origin, copy);
//    }
//
//    @Test
//    public void testZero() {
//        SimState origin = new SimState();
//        origin.collect("s1", new float[]{1,1,1}, new float[]{1,1,1}, new float[]{1,1,1});
//        origin.collect("s2", new float[]{2,2,2}, new float[]{2,2,2}, new float[]{2,2,2});
//        origin.collect("s3", new float[]{3,3,3}, new float[]{3,3,3}, new float[]{3,3,3});
//
//        origin.zero();
//        SimState zero = new SimState();
//        Assert.assertEquals(origin, zero);
//    }
//
//    @Test
//    public void testAdd() {
//        SimState a = new SimState();
//        a.collect("s1", new float[]{1,1,1}, new float[]{1,1,1}, new float[]{1,1,1});
//        a.collect("s2", new float[]{2,2,2}, new float[]{2,2,2}, new float[]{2,2,2});
//        a.collect("s3", new float[]{3,3,3}, new float[]{3,3,3}, new float[]{3,3,3});
//
//        SimState b = new SimState();
//        b.collect("s1", new float[]{10,10,10}, new float[]{10,10,10}, new float[]{10,10,10});
//        b.collect("s2", new float[]{20,20,20}, new float[]{20,20,20}, new float[]{20,20,20});
//
//        SimState c = new SimState();
//        c.collect("s1", new float[]{11,11,11}, new float[]{11,11,11}, new float[]{11,11,11});
//        c.collect("s2", new float[]{22,22,22}, new float[]{22,22,22}, new float[]{22,22,22});
//        c.collect("s3", new float[]{3,3,3}, new float[]{3,3,3}, new float[]{3,3,3});
//
//        SimState r = a.add(b);
//        Assert.assertEquals(r, c);
//    }
//
//    @Test
//    public void testSub() {
//        SimState a = new SimState();
//        a.collect("s1", new float[]{1,1,1}, new float[]{1,1,1}, new float[]{1,1,1});
//        a.collect("s2", new float[]{2,2,2}, new float[]{2,2,2}, new float[]{2,2,2});
//        a.collect("s3", new float[]{3,3,3}, new float[]{3,3,3}, new float[]{3,3,3});
//
//        SimState b = new SimState();
//        b.collect("s1", new float[]{10,10,10}, new float[]{10,10,10}, new float[]{10,10,10});
//        b.collect("s2", new float[]{20,20,20}, new float[]{20,20,20}, new float[]{20,20,20});
//
//        SimState c = new SimState();
//        c.collect("s1", new float[]{11,11,11}, new float[]{11,11,11}, new float[]{11,11,11});
//        c.collect("s2", new float[]{22,22,22}, new float[]{22,22,22}, new float[]{22,22,22});
//        c.collect("s3", new float[]{3,3,3}, new float[]{3,3,3}, new float[]{3,3,3});
//
//        SimState r = c.sub(b);
//        Assert.assertEquals(r, a);
//    }
//
//    @Test
//    public void testMul() {
//        SimState a = new SimState();
//        a.collect("s1", new float[]{1,1,1}, new float[]{1,1,1}, new float[]{1,1,1});
//        a.collect("s2", new float[]{2,2,2}, new float[]{2,2,2}, new float[]{2,2,2});
//        a.collect("s3", new float[]{3,3,3}, new float[]{3,3,3}, new float[]{3,3,3});
//
//        SimState b = new SimState();
//        b.collect("s1", new float[]{10,10,10}, new float[]{10,10,10}, new float[]{10,10,10});
//        b.collect("s2", new float[]{20,20,20}, new float[]{20,20,20}, new float[]{20,20,20});
//        a.collect("s3", new float[]{30,30,30}, new float[]{30,30,30}, new float[]{30,30,30});
//
//        SimState r = a.mul(10);
//        Assert.assertEquals(r, a);
//    }
//
//    @Test
//    public void testArr() {
//        int[] a = new int[1];
//        logger.debug("init: {}", a[0]);
//        changeArr(a);
//        logger.debug("end: {}", a[0]);
//    }
//
//    @Test
//    public void testClazzArr(){
//        int[] a = new int[1];
//        TA ta = new TA();
//        ta.setA(a);
//        a[0] = 1;
//        logger.debug("1: {}", ta.getA()[0]);
//        changeArr(a);
//        logger.debug("2: {}", ta.getA()[0]);
//    }
//
//    public void changeArr(int[] a) {
//        a[0] = 2;
//    }
//
//    public static class TA{
//        int[] a;
//
//        public int[] getA() {
//            return a;
//        }
//
//        public void setA(int[] a) {
//            this.a = a;
//        }
//    }
//}
