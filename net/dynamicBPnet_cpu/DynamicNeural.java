/* 动态神经元类
 * 版本：0.0.0.1
 * 开发时间：2017.4.6
 * 最后一次更改：2017.4.13
 * 最后一次更改内容：无
 */
package net.dynamicBPnet_cpu;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

import saie.neuralnetworks.net.MathUsed;

public class DynamicNeural {
	public boolean openneural=true;
	int funindex=0;

	private ArrayList<Double> power=new ArrayList<Double>();
	private ArrayList<Boolean> linkmark=new ArrayList<Boolean>();
	//偏置
	private double b=0.0;
	
	private double sum=0.0;
	private double out=0.0;
	private double diffout=0.0;
	
	private int NextNeuralAmount=1;

	public DynamicNeural(int NextNeuralAmount) {
		this.NextNeuralAmount=NextNeuralAmount;
	}
	public DynamicNeural(double w_min,double w_max,double b_min,double b_max,int NextNeuralAmount) {
		this.NextNeuralAmount=NextNeuralAmount;
		init_param(w_min,w_max,b_min,b_max);
	}
	public DynamicNeural() {
		this.power.add(1.0);
		linkmark.add(true);
	}
	//随机初始化
	public void init_param() {
		//取随机数
		Random random = new Random();
		this.b=random.nextDouble();
		for(int i=0;i<NextNeuralAmount;i++){
			this.power.add(random.nextDouble());
			this.linkmark.add(true);
		}
	}
	public void init_param(double n) {
		MathUsed tool=new MathUsed();
		this.b=tool.initrandomDouble(n);
		for(int i=0;i<NextNeuralAmount;i++){
			this.power.add(tool.initrandomDouble(n));
			this.linkmark.add(true);
		}
	}
	public void init_param(double w_min,double w_max,double b_min,double b_max) {
		//取随机数
		this.b=new MathUsed().randomDouble(b_min, b_max);
		for(int i=0;i<NextNeuralAmount;i++){
			this.power.add(new MathUsed().randomDouble(w_min, w_max));
			linkmark.add(true);
		}
	}
	// 下一层个数
	public int getNextNeuralAmount() {
		return NextNeuralAmount;
	}
	public void setNextNeuralAmount(int nextNeuralAmount) {
		NextNeuralAmount = nextNeuralAmount;
	}
	//设置偏置
	public void setBias(double b) {
		this.b=b;
	}
	//获取偏置
	public double getBias() {
		return this.b;
	}
	//获取权重
	public double getPower(int index) {
		return power.get(index);
	}
	//获取权重数组
	public ArrayList<Double> getPower() {
		return power;
	}
	public double[] getPowerArray() {
		double[] temp=new double[this.power.size()];
//		Object m=power.toArray();
//		temp=(double[])m;
		for(int i=0;i<this.power.size();i++)
			temp[i]=this.power.get(i);
		return temp;
	}
	//设置权重
	public void setPower(double[] power) {
		this.power.clear();
		double[] _power=new double[power.length];
		for(int i=0;i<power.length;i++){
			_power[i]=power[i];
			this.power.add(i, _power[i]);
		}
	}
	public void setPower(ArrayList<Double> power) {
		this.power = power;
	}
	public void setPower(int index,double power) {
		this.power.set(index, power);
	}
	public void addlink(int index,double power) {
		this.power.add(index,power);
		this.linkmark.add(index,true);
		NextNeuralAmount++;
	}
	public void addlink(double power) {
		this.power.add(power);
		this.linkmark.add(true);
		NextNeuralAmount++;
	}
	public void dellink(int index) {
		this.power.remove(index);
		this.linkmark.remove(index);
		NextNeuralAmount--;
	}
	public void dellink() {
		this.power.remove(NextNeuralAmount-1);
		this.linkmark.remove(NextNeuralAmount-1);
		NextNeuralAmount--;
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
	//输出
	public double output(int index){
		return this.out*(this.power.get(index));
	}
	public double output(){
		return this.out;
	}
	
	public double diffOutput(int index){
		this.diffout=diffFun(this.sum+this.b);
		return this.diffout*(this.power.get(index));
	}
	public double diffOutput(){
		this.diffout=diffFun(this.sum+this.b);
		return this.diffout;
	}
	
	//获取神经元间链接的数组
	public ArrayList<Boolean> getLinkmark() {
		return this.linkmark;
	}
	public boolean[] getLinkmarkArray(){
		boolean[] temp=new boolean[this.linkmark.size()];
		for(int i=0;i<this.linkmark.size();i++)
			temp[i]=this.linkmark.get(i);
		return temp;
	}
	//检测神经元间是否有链接
	public boolean getLinkmark(int i) {
		return linkmark.get(i);
	}
	//设置神经元链接
	public void setLinkmark(ArrayList<Boolean> linkmark) {
		this.linkmark = linkmark;
	}
/*************************************************************************/
	ForkJoinPool forkjoinPool = new ForkJoinPool();
	
	//输入-1 神经元层输入
	public double noCheckInput(DynamicNeural[] cell,int index) {
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
	public double input(DynamicNeural[] cell,int index) {
		DoInputNeuralCountTask task=new DoInputNeuralCountTask(0, cell.length-1, cell, index);
		Future<Double> result = forkjoinPool.submit(task);
		//System.out.println("OK!");
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
	
//	public double input(DynamicNeural[] cell,int index) {
//		this.sum=0;
//  		for(int i=0;i<cell.length;i++)
//  			if(cell[i].checkNeural())
//  				if(cell[i].checkLink(index))
//  					sum+=cell[i].output(index);
//		this.out=fun(this.sum+this.b);
//		return this.sum;
//	}
	
	public double input(ArrayList<DynamicNeural> _cell,int index) {
		DynamicNeural[] cell=new DynamicNeural[_cell.size()];
		for(int i=0;i<_cell.size();i++)
			cell[i]=_cell.get(i);
		return this.input(cell,index);
	}
	public double input(DynamicNeural[] cell,int index,int start,int end) {
		DoInputNeuralCountTask task=new DoInputNeuralCountTask(start, end, cell, index);
		Future<Double> result = forkjoinPool.submit(task);
		//System.out.println("OK!");
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
	public double input(ArrayList<DynamicNeural> _cell,int index,int start,int end) {
		DynamicNeural[] cell=new DynamicNeural[_cell.size()];
		for(int i=0;i<_cell.size();i++)
			cell[i]=_cell.get(i);
		return this.input(cell,index,start,end);
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
	public double noFunInput(DynamicNeural[] cell,int index) {
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
/*************************************************************************/
	//关闭
	public void close() {
		this.openneural=false;
	}
	public void closeLink(int index) {
		this.linkmark.set(index, false);
	}
	//开启
	public void open() {
		this.openneural=true;
	}
	public void openLink(int index) {
		this.linkmark.set(index, true);
	}
	//检测
	public boolean checkNeural() {
		return this.openneural;
	}
	public boolean checkLink(int index) {
		return this.linkmark.get(index);
	}
}
//并行计算模块
class DoInputNeuralCountTask extends RecursiveTask<Double>{

	/**
	 *  加法并行计算类
	 */
	private static final long serialVersionUID = -7991088658428350407L;

	public static final int threshold = MathUsed.neuralthreshold;
	
	DynamicNeural[] cell;
  private int start;
  private int end;
	private int index;
	public DoInputNeuralCountTask(int start, int end,DynamicNeural[] cell,int index) {
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
	private static final long serialVersionUID = 1550689983790007783L;

	public static final int threshold = MathUsed.neuralthreshold;
	
	DynamicNeural[] cell;
	private int start;
	private int end;
	private int index;
	public DoNCInputNeuralCountTask(int start, int end,DynamicNeural[] cell,int index) {
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
	private static final long serialVersionUID = -6395759885781677317L;
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
	private static final long serialVersionUID = 23815531834550044L;
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
