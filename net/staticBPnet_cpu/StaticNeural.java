/* 静态神经元类
 * 版本：0.0.0.1
 * 开发时间：2017.3.29
 * 
 * 最后一次更改：2017.4.12
 * 更改内容：无
 */
package saie.neuralnetworks.net.staticBPnet_cpu;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;

import saie.neuralnetworks.net.MathUsed;

public class StaticNeural {
	public boolean openneural=true;
	//函数索引
	int funindex=0;
	//权重数组
	private double[] power=new double[1];	
	//链接标记
	private boolean[] linkmark=new boolean[1];
	//偏置
	private double b=0.0;
	
	private double sum=0.0;
	private double out=0.0;
	private double diffout=0.0;
	
	int NextNeuralAmount=1;
	
	//构造方法
	public StaticNeural(int NextNeuralAmount) {
		this.NextNeuralAmount=NextNeuralAmount;
		this.power=new double[NextNeuralAmount];
		this.linkmark=new boolean[NextNeuralAmount];
	}
	public StaticNeural(double w_min,double w_max,double b_min,double b_max,int NextNeuralAmount) {
		this.NextNeuralAmount=NextNeuralAmount;
		this.power=new double[NextNeuralAmount];
		this.linkmark=new boolean[NextNeuralAmount];
		init_param(w_min,w_max,b_min,b_max);
	}
	public StaticNeural(int NextNeuralAmount,int n) {
		this.NextNeuralAmount=NextNeuralAmount;
		this.power=new double[NextNeuralAmount];
		this.linkmark=new boolean[NextNeuralAmount];
		switch(n){
			case 0:init_param();break;//随机初始化
			case 1:_init_param(0);break;//条件初始化-1
			case 2:_init_param(1);break;//条件初始化-2
			default:init_param();
		}
	}
	public StaticNeural() {
		this.power[0]=1.0;
		linkmark[0]=true;
	}
	
	//初始化参数-1  随机初始化
	public void init_param() {
		//取随机数
		Random random = new Random();
		this.b=random.nextDouble();
		for(int i=0;i<this.power.length;i++){
			this.power[i]=random.nextDouble();
			this.linkmark[i]=true;
		}
	}
	public void init_param(int n) {
		MathUsed tool=new MathUsed();
		this.b=tool.initrandomDouble(n);
		for(int i=0;i<this.power.length;i++){
			this.power[i]=tool.initrandomDouble(n);
			this.linkmark[i]=true;
		}
	}
	public void init_param(double w_min,double w_max,double b_min,double b_max) {
		//取随机数
		this.b=new MathUsed().randomDouble(b_min, b_max);
		for(int i=0;i<this.power.length;i++){
			this.power[i]=new MathUsed().randomDouble(w_min, w_max);
			linkmark[i]=true;
		}
	}
	//初始化参数-2  条件初始化
	public void _init_param(int a) {
		for(int i=0;i<this.power.length;i++){
			this.power[i]=a;
			linkmark[i]=true;
		}
	}
	//获取下一层的神经元个数
	public int getNextNeuralAmount() {
		return NextNeuralAmount;
	}
	//设置下一层神经元个数
	public void setNextNeuralAmount(int NextNeuralAmount) {
		this.power=new double[NextNeuralAmount];
	}
	public double getSum() {
		return sum;
	}
	//设置权重
	public void setPower(int index,double power) {
		this.power[index]= power;
	}
	public void setPower(double[] power) {
		this.power= power;
	}
	//获取权重
	public double[] getPower() {
		return this.power;
	}
	public double getPower(int index) {
		return this.power[index];
	}
	//设置偏置
	public void setBias(double b) {
		this.b=b;
	}
	//获取偏置
	public double getBias() {
		return this.b;
	}
	// 设置函数
	public double fun(double sum) {
		return MathUsed.getFun(sum,this.funindex);
	}
	private double diffFun(double sum) {
		return MathUsed.getFun(sum,this.funindex);
	}
	public void setfun(int funindex) {
		this.funindex=funindex;
	}
	//获取函数类型
	public int getFunindex() {
		return this.funindex;
	}
	//输出
	public double output(int index){
		return this.out*(this.power[index]);
	}
	public double output(){
		return this.out;
	}
	public double diffOutput(int index){
		this.diffout=diffFun(this.sum+this.b);
		return this.diffout*(this.power[index]);
	}
	public double diffOutput(){
		this.diffout=diffFun(this.sum+this.b);
		return this.diffout;
	}
	ForkJoinPool forkjoinPool = new ForkJoinPool();
	
	//输入-1 神经元层输入
	public double noCheckInput(StaticNeural[] cell,int index) {
		DoNCInputNeuralCountTask task=new DoNCInputNeuralCountTask(0, cell.length-1, cell, index);
		Future<Double> result = forkjoinPool.submit(task);
		try {
			this.sum=result.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		this.out=fun(this.sum+b);
		return this.sum;
	}
	public double input(StaticNeural[] cell,int index) {
		DoInputNeuralCountTask task=new DoInputNeuralCountTask(0, cell.length-1, cell, index);
		Future<Double> result = forkjoinPool.submit(task);
		try {
			this.sum=result.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		this.out=fun(this.sum+this.b);
		return this.sum;
	}
	public double input(StaticNeural[] cell,int index,int start,int end) {
		DoInputNeuralCountTask task=new DoInputNeuralCountTask(start, end, cell, index);
		Future<Double> result = forkjoinPool.submit(task);
		try {
			this.sum=result.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		this.out=fun(this.sum+this.b);
		return this.sum;
	}
	//输入-2 初始层输入
	public double input(double[] cell,double[] power) {
		SrcDataInputNeuralCountTask task=new SrcDataInputNeuralCountTask(0, power.length-1, cell, power);
		Future<Double> result = forkjoinPool.submit(task);
		try {
			this.sum=result.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		this.out=fun(this.sum+b);
		return this.sum;
	}
	public double input(double[] cell,double[] power,int start,int end) {
		SrcDataInputNeuralCountTask task=new SrcDataInputNeuralCountTask(start, end, cell, power);
		Future<Double> result = forkjoinPool.submit(task);
		try {
			this.sum=result.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		this.out=fun(this.sum+b);
		return this.sum;
	}
	public double input(double[] cell){
		DataInputNeuralCountTask task=new DataInputNeuralCountTask(0, cell.length-1, cell);
		Future<Double> result = forkjoinPool.submit(task);
		try {
			this.sum=result.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		this.out=fun(this.sum+this.b);
		return this.sum;
	}
	//输入-3 作为输入层
	public void input(double data){
		this.out=data;
	}
	//无函数计算输入
	public double noFunInput(StaticNeural[] cell,int index) {
		DoInputNeuralCountTask task=new DoInputNeuralCountTask(0, cell.length-1, cell, index);
		Future<Double> result = forkjoinPool.submit(task);
		try {
			this.sum=result.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return this.sum;
	}
	//关闭
	public void close() {
		this.openneural=false;
	}
	public void closeLink(int index) {
		this.linkmark[index]=false;
	}
	//开启
	public void open() {
		this.openneural=true;
	}
	public void openLink(int index) {
		this.linkmark[index]=true;
	}
	//检测
	public boolean checkNeural() {
		return this.openneural;
	}
	public boolean checkLink(int index) {
		return this.linkmark[index];
	}
}
//并行计算模块
class DoInputNeuralCountTask extends RecursiveTask<Double>{
	/**
	 *  加法并行计算类
	 */
	private static final long serialVersionUID = -5629221511599716497L;
	public static final int threshold = MathUsed.neuralthreshold;
	
	StaticNeural[] cell;
    private int start;
    private int end;
	private int index;
	public DoInputNeuralCountTask(int start, int end,StaticNeural[] cell,int index) {
        this.start = start;
        this.end = end;
		this.cell=cell;
		this.index=index;
	}	
	protected Double compute() {
		double sum=0.0;
        //如果任务足够小就计算任务
        boolean canCompute = ((this.end - this.start) <= threshold);
        if(canCompute)
    		for(int i=this.start;i<=this.end;i++)
    			if(this.cell[i].checkNeural())
    				if(this.cell[i].checkLink(index))
    					sum+=this.cell[i].output(this.index);
        else
        {
            // 如果任务大于阈值，就分裂成两个子任务计算
            int middle = (start + end)/2;
            DoInputNeuralCountTask leftTask = new DoInputNeuralCountTask(this.start, middle,this.cell,this.index);
            DoInputNeuralCountTask rightTask = new DoInputNeuralCountTask(middle+1,this.end,this.cell,this.index);
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
class DoNCInputNeuralCountTask extends RecursiveTask<Double>{
	/**
	 *  无检查加法并行计算类
	 */
	private static final long serialVersionUID = -6522349801359715693L;
	public static final int threshold = MathUsed.neuralthreshold;
	
	StaticNeural[] cell;
    private int start;
    private int end;
	private int index;
	public DoNCInputNeuralCountTask(int start, int end,StaticNeural[] cell,int index) {
        this.start = start;
        this.end = end;
		this.cell=cell;
		this.index=index;
	}	
	protected Double compute() {
		double sum=0.0;
        //如果任务足够小就计算任务
        boolean canCompute = ((this.end - this.start) <= threshold);
        if(canCompute)
    		for(int i=this.start;i<=this.end;i++)
    				sum+=this.cell[i].output(this.index);
        else
        {
            // 如果任务大于阈值，就分裂成两个子任务计算
            int middle = (start + end)/2;
            DoNCInputNeuralCountTask leftTask = new DoNCInputNeuralCountTask(this.start, middle,this.cell,this.index);
            DoNCInputNeuralCountTask rightTask = new DoNCInputNeuralCountTask(middle+1,this.end,this.cell,this.index);
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

class SrcDataInputNeuralCountTask extends RecursiveTask<Double>{
	/**
	 *  源数据加法并行计算类-1
	 */
	private static final long serialVersionUID = -6123098256838278246L;
    public static final int threshold = MathUsed.neuralthreshold;

    private double[] cell;
    private double[] power;
    private int start;
    private int end;
    public SrcDataInputNeuralCountTask(int start, int end,double[] cell,double[] power)
    {
        this.start = start;
        this.end = end;
        this.cell=cell;
        this.power=power;
    }
	protected Double compute() {
		double sum=0.0;
        //如果任务足够小就计算任务
        boolean canCompute = ((this.end - this.start) <= threshold);
        if(canCompute)
    		for(int i=0;i<cell.length;i++)
    			for(int j=this.start;j<=this.end; j++)
    				sum+=this.cell[i]*this.power[j];
        else
        {
            // 如果任务大于阈值，就分裂成两个子任务计算
            int middle = (this.start + this.end)/2;
            SrcDataInputNeuralCountTask leftTask = new SrcDataInputNeuralCountTask(this.start, middle,this.cell,this.power);
            SrcDataInputNeuralCountTask rightTask = new SrcDataInputNeuralCountTask(middle+1, this.end,this.cell,this.power);
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

class DataInputNeuralCountTask extends RecursiveTask<Double>{
	/**
	 *  源数据加法并行计算类-2
	 */
	private static final long serialVersionUID = -9093592245807388128L;
    public static final int threshold = MathUsed.neuralthreshold;

    private double[] data;
    private int start;
    private int end;
    public DataInputNeuralCountTask(int start, int end,double[] data)
    {
        this.start = start;
        this.end = end;
        this.data=data;
    }
	protected Double compute() {
		double sum=0.0;
        //如果任务足够小就计算任务
        boolean canCompute = ((this.end - this.start) <= threshold);
        if(canCompute)
    			for(int i=this.start;i<=this.end; i++)
    				sum+=this.data[i];
        else
        {
            // 如果任务大于阈值，就分裂成两个子任务计算
            int middle = (this.start + this.end)/2;
            DataInputNeuralCountTask leftTask = new DataInputNeuralCountTask(this.start, middle,this.data);
            DataInputNeuralCountTask rightTask = new DataInputNeuralCountTask(middle+1, this.end,this.data);
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