package Weapon;


import cn.nukkit.level.Position;
import cn.nukkit.level.particle.DustParticle;
import cn.nukkit.level.particle.EnchantmentTableParticle;
import cn.nukkit.math.Vector3;


import java.util.ArrayList;

public class Effects {
    //减伤特效
    public static void addRelief(Position position){
        ArrayList<Double[]> a = new ArrayList<>();
        ArrayList<Double[]> pos = new ArrayList<>();
        for(int i = 0;i <= 90;i += 9){
            double x = 1.5 * Math.cos(Math.toRadians(i));
            double y = 1.5 * Math.sin(Math.toRadians(i));
            a.add(new Double[]{x,+y});
            a.add(new Double[]{x,-y});
        }

        for(Double[] b : a){
            for(int i = 0;i <= 90;i += 9){
                double x = b[0] * Math.cos(Math.toRadians(i));
                double z= b[0] * Math.sin(Math.toRadians(i));
                pos.add(new Double[]{x,b[1],z});
                pos.add(new Double[]{-z,b[1],x});
                pos.add(new Double[]{-x,b[1],-z});
                pos.add(new Double[]{z,b[1],-x});
            }
        }
        for (Double[] xyz:pos){
            position.level.addParticle(new EnchantmentTableParticle(new Vector3(xyz[0]+ position.x,xyz[1]+ position.y+1,xyz[2]+position.z)));
        }
    }
    //回血特效
    public static void addHealth(Position position){
        ArrayList<Double[]> pos = new ArrayList<>();
        double sin1 = Math.sin(Math.toRadians(18));
        double cos1 = Math.cos(Math.toRadians(18));
        double sin2 = Math.sin(Math.toRadians(36));
        double cos2 = Math.cos(Math.toRadians(36));
        double l = 2 * 1.5 * cos1;
        for(double i=-1.5 * cos1;i<=1.5 * cos1;i+=0.1){
            pos.add(new Double[]{i,0.0,1.5*sin1});
        }

        for(double i=0; i <= l;i+=0.1){
            double x = i * cos1;
            double z = i * sin1;
            pos.add(new Double[]{-z,0.0,1.5-x});
            pos.add(new Double[]{z,0.0,1.5,-x});
            x = i * cos2;
            z = i * sin2;
            pos.add(new Double[]{1.5*cos1-x,0.0,1.5*sin1-z});
            pos.add(new Double[]{-1.5*cos1+x,0.0,1.5*sin1-z});
        }
        for (Double[] xyz:pos){
            position.level.addParticle(new DustParticle(new Vector3(xyz[0]+ position.x,xyz[1]+ position.y+1,xyz[2]+position.z),250,0,0,250));
        }
    }

    public static void addIce(Position position){

        ArrayList<Double[]> a = new ArrayList<>();
        ArrayList<Double[]> pos = new ArrayList<>();
        double rr = 1.5 * 0.2;
        for(int i=0;i <= 90;i += 10){
            double x = rr*Math.cos(Math.toRadians(i));
            double y = rr*Math.sin(Math.toRadians(i));
            a.add(new Double[]{x,y});
            a.add(new Double[]{x,-y});
            a.add(new Double[]{-x,y});
            a.add(new Double[]{-x,-y});
        }
        for(Double[] b : a){
            for(int i = 0;i <= 90;i+=10){
                double x=(1.5-b[0])*Math.cos(Math.toRadians(i));
                double z=(1.5-b[0])*Math.sin(Math.toRadians(i));
                pos.add(new Double[]{x,b[1],z});
                pos.add(new Double[]{-z,b[1],x});
                pos.add(new Double[]{-x,b[1],-z});
                pos.add(new Double[]{z,b[1],-x});
            }
        }
        for (Double[] xyz:pos){
            position.level.addParticle(new DustParticle(new Vector3(xyz[0]+ position.x,xyz[1]+ position.y+2,xyz[2]+position.z),0,0,250,250));
        }

    }

}
