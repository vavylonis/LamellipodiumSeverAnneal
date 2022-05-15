package main;

import java.awt.FileDialog;
import java.awt.Frame;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.opensourcephysics.numerics.Quaternion;
import org.opensourcephysics.numerics.Transformation;
//import java.math.BigDecimal;

/**
 * Container of utility functions, primarily concerning vector math
 * @author Aaron Hall
 */
public class MHelp {
	public static Random rand = new Random();//used for random number generation throughout 
	public static double avaNum = 602.2141;//avagadro's number to keep value in uM when converting from /um^3, not actually avagadro's number
	/**
	 * Opens standard file dialog window for file saving/opening
	 * @param mode : use FileDialog.LOAD or FileDialog.SAVE
	 * @return filename including path
	 */
	public static String fileDialog(int mode) {
		Frame parent = null;
		FileDialog fd = new FileDialog(parent, "File Dialog", mode);
		fd.setVisible(true);
		String fname = fd.getFile();
		String dirname = fd.getDirectory();
		String fullname = dirname + fname;
		return fullname;
	}
	//CALCULATIONS**************************************************
    /**
     * Generates random angle in radians between 0 and 2PI
     * @return
     */
	protected double randAng(){
    	double r=rand.nextDouble();
    	return r*2*Math.PI;
    }
    
    //MATRIX CALCULATIONS*******************************************
	/**
	 * Vector subtraction
	 * @param a
	 * @param b
	 * @return
	 */
    protected static double[] vecDiff( double[] a, double[] b){
        double[] c = new double[3];
        for(int i = 0; i<3; i++)
            c[i] = a[i] - b[i];      
        return c;
    }
    /**
     * Vector addition
     * @param a
     * @param b
     * @return
     */
    protected static double[] vecSum( double[] a, double[] b){
        double[] c = new double[3];
        for(int i = 0; i<3; i++)
            c[i] = a[i] + b[i];
        return c;
    }
    /**
     * Dot product
     * @param a
     * @param b
     * @return
     */
    protected static double vecProd(double[] a, double[] b){
    	double c=0;
    	for(int i=0;i<3;i++){
    		c+=a[i]*b[i];
    	}
    	return c;
    	
    }
    /**
     * Vector normalization
     * @param a
     * @return
     */
    protected static double[] vecNorm(double[] a){
        double mag = mag(a);  
        double[] c = new double[a.length];
        for(int i = 0; i<a.length; i++)
            c[i] = a[i]/mag;
        return c;
    }
    /**
     * Vector magnitude
     * @param a
     * @return
     */
    protected static double mag(double[] a){
        double mag = 0;
        for(int i = 0; i<a.length; i++)
            mag += Math.pow(a[i],2);
        return Math.sqrt(mag);
    }
    /**
     * Vector Cross Product
     * @param a
     * @param b
     * @return
     */
    protected static double[] vecXProd(double[] a, double[] b){
    	double[] c = new double[3];
        for(int i = 0; i<3; i++)
            c[i] = a[(i+1)%3]*b[(i+2)%3] - a[(i+2)%3]*b[(i+1)%3]; 
        //c[1] = -c[1];  ????????????????
        return c;       
    }
    /**
     * Change of basis
     * @param basis
     * @param a
     * @return
     */
    protected static double[] matProd(double[][] basis, double[] a){
        double[] b = new double[3];        
        for(int i = 0; i<3; i++){           
            for(int j = 0; j<3; j++)
                b[i] += basis[i][j]*a[j];         
        }    
        return b;     
    }
    /**
     * Return basis defined by three vectors
     * @param t
     * @param n
     * @param b
     * @return
     */
    protected static double[][] basis(double[] t, double[] n, double[] b){
        double[][] basis = new double[3][3];  
        for(int i = 0; i<3; i++){
            basis[i][0] = t[i];
            basis[i][1] = n[i];
            basis[i][2] = b[i];
        } 
        return basis;
    }
   //constant overloads
    /**
     * Vector minus a constant
     * @param a
     * @param b
     * @return
     */
    protected static double[] vecDiff( double[] a, double b){
        double[] c = new double[3];
        for(int i = 0; i<3; i++)
            c[i] = a[i] - b;      
        return c;
    }
    /**
     * Vector plus a constant
     * @param a
     * @param b
     * @return
     */
    protected static double[] vecSum( double[] a, double b){
        double[] c = new double[3];
        for(int i = 0; i<3; i++)
            c[i] = a[i] + b;
        return c;
    }
    /**
     * Vector multiplied by a constant
     * @param a
     * @param b
     * @return
     */
    protected static double[] vecProd(double[] a, double b){
    	double c[] = new double[3];
    	for(int i=0;i<a.length;i++){
    		c[i]=a[i]*b;
    	}
    	return c;
    }
    /**
     * Round a double, d, to specified decimal places, n
     * @param d
     * @param n
     * @return
     */
    protected static double round(double d, int n){
    	//** rounds d to decimal place n
    	return Math.round(d*Math.pow(10, n))/Math.pow(10, n);
    }
    /**
     * Generates a friendly filename that includes time and date
     * @param prepend
     * @param ext
     * @return
     */
    protected static String fname(String prepend, String ext){
    	Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss") ;
		String fname = prepend+"_date_"+dateFormat.format(date)+ext;
    	return fname;
    }
    /**
     * Find the shortest vector connecting two finite line segments
     * @param p1 endpoint of line 1
     * @param p2 endpoint of line 1
     * @param p3 endpoint of line 2
     * @param p4 endpoint of line 2
     * @return 
     */
    protected static double[] DistBetween2Segment(double[] p1, double[] p2, double[] p3, double[] p4){
    	//returns vector drawn from line 2 towards line 1
    	//Adapted from MATLAB 
    	//https://www.mathworks.com/matlabcentral/fileexchange/32487-shortest-distance-between-two-line-segments
	    double[] u = vecDiff(p1,p2);
	    double[] v = vecDiff(p3,p4);
	    double[] w = vecDiff(p2,p4);
	      
	    double a = vecProd(u,u);
	    double b = vecProd(u,v);
	    double c = vecProd(v,v);
	    double d = vecProd(u,w);
	    double e = vecProd(v,w);
	    double D = a*c - b*b;
	    double sD = D;
	    double tD = D;
	    
	    double SMALL_NUM = 0.00000001;
	    
	    //compute the line parameters of the two closest points
	    double sN;
	    double tN;
	    if (D < SMALL_NUM){// the lines are almost parallel
	        sN = 0.0;// force using point P0 on segment S1
	        sD = 1.0;// to prevent possible division by 0.0 later
	        tN = e;
	        tD = c;
	    }else{//get the closest points on the infinite lines
	        sN = (b*e - c*d);
	        tN = (a*e - b*d);
	        if (sN < 0.0){//sc < 0 => the s=0 edge is visible       
	            sN = 0.0;
	            tN = e;
	            tD = c;
	        }else if(sN > sD){//sc > 1 => the s=1 edge is visible
	            sN = sD;
	            tN = e + b;
	            tD = c;
	        }
	    }
	    
	    if (tN < 0.0){//tc < 0 => the t=0 edge is visible
	        tN = 0.0;
	        //recompute sc for this edge
	        if(-d < 0.0){
	            sN = 0.0;
	        }else if(-d > a){
	            sN = sD;
	        }else{
	            sN = -d;
	            sD = a;
	        }
	    }else if(tN > tD){//tc > 1 => the t=1 edge is visible
	        tN = tD;
	        //recompute sc for this edge
	        if((-d + b) < 0.0){
	            sN = 0;
	        }else if((-d + b) > a){
	            sN = sD;
	        }else{
	            sN = (-d + b);
	            sD = a;
	        }
	    }
	    double sc,tc;
	    //finally do the division to get sc and tc
	    if(Math.abs(sN) < SMALL_NUM){
	        sc = 0.0;
	    }else{
	        sc = sN / sD;
	    }
	    
	    if(Math.abs(tN) < SMALL_NUM){
	        tc = 0.0;
	    }else{
	        tc = tN / tD;
	    }
	    
	    //get the difference of the two closest points
	    double[] dP = vecDiff(vecSum(w,vecProd(u,sc)),vecProd(v,tc)); // = S1(sc) - S2(tc)

	    return dP;
	}
    /**
     * Returns quaternion rotations that aligns vector direction with vector target
     * @param direction
     * @param target
     * @return Quaternion Transformation
     */
    protected static Transformation rotationTransform(double[] direction, double[] target){
    	double[] xyz = MHelp.vecXProd(direction, target);
    	double w = MHelp.vecProd(direction, target) + Math.sqrt((Math.pow(MHelp.mag(direction), 2)) * (Math.pow(MHelp.mag(target), 2)));
    	Quaternion ret = new Quaternion(w,xyz[0],xyz[1],xyz[2]);
		ret.normalize();
    	return ret;
    }
    /**
     * Prints double arrays in a friendly way
     * @param arr
     */
    protected static void print_double_array(double[] arr){
    	for(int i=0;i<arr.length;i++){
    		double d = arr[i];
    		System.out.print(d);
    		if(i<arr.length-1) System.out.print(",");
    	}
    	System.out.println("");
    }
    /**
     * Distance between two 2d points
     * @param p1
     * @param p2
     * @return
     */
    protected static double dist_between_points(double[] p1, double[] p2){
    	return Math.sqrt(Math.pow(p1[0]-p2[0], 2)+Math.pow(p1[1]-p2[1], 2)+Math.pow(p1[2]-p2[2], 2));
    }
    /**
     * Uses quadratic equation to find roots of a binomial
     * @param a
     * @param b
     * @param c
     * @return
     */
    protected static double[] binomial_roots(double a, double b, double c){
        double answer1 = (-b + Math.sqrt(Math.pow(b, 2) - (4 * a * c))) / (2 * a);
        double answer2 = (-b - Math.sqrt(Math.pow(b, 2) - (4 * a * c))) / (2 * a);
        if(Double.isNaN(answer1)&&Double.isNaN(answer2)){
        	return null;
        }else if(Double.isNaN(answer1)){
        	return new double[]{answer2};
        }else if(Double.isNaN(answer2)){
        	return new double[]{answer1};
        }else{
        	return new double[]{answer1,answer2};
        }
    }
    /**
     * Rotates vector around a rotation vector. Returned vector is normalized.
     * @param vec
     * @param rot_axis
     * @param angle
     * @return
     */
    protected static double[] rotate_vector(double[] vec, double[] rot_axis, double angle){
    	// quaternion rotation 
    	double[] ret = vec.clone();
    	ret = MHelp.vecNorm(ret);
		double cos=Math.cos(angle/2.0),sin=Math.sin(angle/2.0);
		Quaternion rotation = new Quaternion(cos,sin*rot_axis[0],sin*rot_axis[1],sin*rot_axis[2]);
		rotation.direct(ret);
		return ret;
    }
}
