/* 数学类
 * 版本：0.0.0.1
 * 开发时间：2017.3.29
 * 
 * 最后一次更改：2017.4.5
 * 更改内容：无
 */
package saie.neuralnetworks.net;

import java.util.Random;
import java.util.concurrent.RecursiveTask;

public class MathUsed {
	
	public static final int neuralthreshold=160;
	public static int counttaskthreshold=160;
	
	private Random randomtool=new Random();
	public MathUsed() {
		// TODO Auto-generated constructor stub
	}
	//神经元处理类函数
	/**************************************************************************/
	public static double getFun(double sum, int funindex) {
		switch(funindex){
			case -1:return DIYFun(sum);
			case 0:return sigmoid(sum);
			case 1:return halftanh(sum);
			default:return sigmoid(sum);
		}
	}
	/*************/
	public static double DIYFun(double x) {
		
		return 0;
	}
	public static double sigmoid (double x){
		return 1.0/(1.0+Math.exp(-x));
	}
	public static double halftanh (double x){
		return (Math.tanh(x)+1.0)/2;
	}
	/*************/
	public static double getFunDiff(double num, int funindex) {
		switch(funindex){
			case -1:return DIYFunDiff(num);
			case 0:return sigmoidDiff(num);
			case 1:return halftanhDiff(num);
			default:return sigmoidDiff(num);
		}
	}
	/*************/
	public static double DIYFunDiff(double x) {
		double dx=0.00001;
		return (DIYFun(x+dx)-DIYFun(x))/dx;
	}
	public static double sigmoidDiff(double x){
		return (1.0-1.0/(1.0+Math.exp(-x)))/(1.0+Math.exp(-x));
	}
	public static double halftanhDiff(double x){
		return (1.0/2.0)*(1.0-Math.pow(Math.tanh(x),2));
	}
	/**************************************************************************/	
	
	//梯度下降步伐大小函数
	/**************************************************************************/
	public static double getMovesizeFun(int n,double... num) {
		switch(n){
			case -1:return DIYMovesizeFun(num);
			case 0:
			default:return 0.01;
		}
	}
	public static double DIYMovesizeFun(double[] num){
		
		return 0;
	}
	
	
	
	
	
	
	
	
	
	/**************************************************************************/
	
	/**************************************************************************/
	public static int LayerDifficultyLevel(int num1,int num2) {
		return (int)(counttaskthreshold*Math.exp(-Math.sqrt(num1*num2)/counttaskthreshold))+2;
	}
	public double randomDouble(double num1,double num2) {
		return (this.randomtool.nextDouble()*(num2-num1))+num1;
	}
	public double initrandomDouble(double num) {
		num=3.0/Math.sqrt(num);
		return (this.randomtool.nextDouble()*2.0*(num))+num;
	}
	/**************************************************************************/
}

class CountTask extends RecursiveTask<Double>{
	/**
	 *  加法并行计算类
	 */
	private static final long serialVersionUID = -9093592245807388128L;
    public static final int threshold = MathUsed.counttaskthreshold;

    private double[] data;
    private double[] power;
    private int start;
    private int end;
    public CountTask(int start, int end,double[] data,double[] power)
    {
        this.start = start;
        this.end = end;
        this.data=data;
        this.power=power;
    }
	protected Double compute() {
		double sum=0.0;
        //如果任务足够小就计算任务
        boolean canCompute = ((end - start) <= threshold);
        if(canCompute)
        {
            for (int i=start;i<=end; i++)
                sum += this.data[i]*this.power[i];
        }
        else
        {
            // 如果任务大于阈值，就分裂成两个子任务计算
            int middle = (start + end)/2;
            CountTask leftTask = new CountTask(start, middle,this.data,power);
            CountTask rightTask = new CountTask(middle+1, end,this.data,power);
            // 执行子任务
            leftTask.fork();
            rightTask.fork();
            //等待任务执行结束合并其结果
            Double leftResult = leftTask.join();
            Double rightResult = rightTask.join();   
            //合并子任务
            sum = leftResult + rightResult;
        }
		return sum;
	}	
}