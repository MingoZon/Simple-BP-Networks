/* 动态神经网络类
 * 版本：0.0.0.1
 * 开发时间：2017.4.11
 * 最后一次更改：2017.4.13
 * 最后一次更改内容：无
 */
package saie.neuralnetworks.net.dynamicBPnet_cpu;

import java.util.ArrayList;
import java.util.Random;

import saie.neuralnetworks.net.MathUsed;


public class DynamicNetworks {
	private ArrayList<Integer> amount = new ArrayList<Integer>();
	private int numberOfLayers=1;
	private ArrayList<ArrayList<DynamicNeural>> cellGroup = new ArrayList<ArrayList<DynamicNeural>>();
	//输出层包含的神经元个数
	private int numberOfOutputNeural;
	//梯度移动距离
	private double movesize=0.1;
	
	private double movesizeMAX=1.00;
	private double movesizeMIN=0.0;
	private boolean movesizeRandom=false;
	private boolean movesizeFun=false;
	
	private int movesizeFunIndex=0;
	//权重集
	private double[][][] power;
	private double[][] bias;
	
	//权重相关标记
	private boolean powerCreateMark=false;
	private boolean powerDataMark=false;
	//偏置相关标记
	private boolean biasCreateMark=false;
	private boolean biasDataMark=false;
	//输出层输出
	private double[] output;
	private double threshold=0.5;
	//构造函数-1 创造神经网络
	public DynamicNetworks(int... amount) {
		for(int i=0;i<amount.length;i++)
			this.amount.add(amount[i]);
		this.numberOfLayers=amount.length;
		this.numberOfOutputNeural=amount[amount.length-1];
		this.output = new double[amount[numberOfLayers-1]];
		this._Error=new double[amount[numberOfLayers-1]];
		createNetworks(amount);
	}
	
	//构造函数-2 载入神经网络
	public DynamicNetworks(double[][][] power,double[][] bias) {
		this.power=power;
		this.bias=bias;
//		this.powerDataMark=true;
//		this.powerDataMark=true;
//		this.biasCreateMark=true;
//		this.biasDataMark=true;
		this.numberOfLayers=power.length;
		//加载神经网络
		
		int[] _tempAmount=new int[numberOfLayers];
		for(int i=0;i<numberOfLayers;i++){
			this.amount.add(power[i].length);
			_tempAmount[i]=power[i].length;
		}
		
		this.numberOfOutputNeural=amount.get(numberOfLayers-1);
		this.output = new double[amount.get(numberOfLayers-1)];
		this._Error=new double[amount.get(numberOfLayers-1)];
		
		double _tempPower;
		createNetworks(_tempAmount);
		for(int i=0;i<numberOfLayers;i++)
			for(int j=0;j<_tempAmount[i];j++)
				for(int k=0;k<cellGroup.get(i).get(j).getPower().size();k++){
					_tempPower=power[i][j][k];
					cellGroup.get(i).get(j).setPower(k, _tempPower);
				}
//		@SuppressWarnings("unchecked")
//		ArrayList<DynamicNeural>[] layer=new ArrayList[numberOfLayers];
//		
//		for(int i=0;i<numberOfLayers-1;i++) {
//			layer[i]=new ArrayList<DynamicNeural>();
//			for (int j=0;j<amount.get(i);j++){
//					DynamicNeural temp=new DynamicNeural(amount.get(i+1));
//					temp.setPower(power[i][j]);
//					temp.setBias(bias[i][j]);
//					layer[i].add(temp);
//			}
//			cellGroup.add(layer[i]);
//		}
//		
//		layer[numberOfLayers-1]=new ArrayList<DynamicNeural>();
//		for (int i=0;i<amount.get(numberOfLayers-1);i++){
//			DynamicNeural temp=new DynamicNeural();
//			temp.setPower(power[numberOfLayers-1][i]);
//			temp.setBias(bias[numberOfLayers-1][i]);
//			layer[numberOfLayers-1].add(temp);
//		}
//		cellGroup.add(layer[numberOfLayers-1]);
//		
	}
	//创造网络
	private void createNetworks(int[] amount) {
		//输入层
		cellGroup.add(_Gloop(amount[0],0,amount[1],0));
		// 隐藏层
		for (int i=1;i<amount.length-1;i++)
			cellGroup.add(_Gloop(amount[i],amount[i-1],amount[i+1],1));
		//输出层
			cellGroup.add(_Gloop(amount[numberOfLayers-1],0,0,2));
	}
	// 数组填充
	private ArrayList<DynamicNeural> _Gloop(int amount,int shangAmount,int nextAmount,int n){
		ArrayList<DynamicNeural> tempcell=new ArrayList<DynamicNeural>();
		if(n==0)
			for (int i=0;i<amount;i++) tempcell.add(_Neuralmaker(n,nextAmount));
		else if(n==1)
			for (int i=0;i<amount;i++) tempcell.add(_Neuralmaker(n,shangAmount,nextAmount));
		else if(n==2)
			for (int i=0;i<amount;i++) tempcell.add(_Neuralmaker(n));
		return tempcell;
	}
	//创造神经元
	private DynamicNeural _Neuralmaker(int... num){
		DynamicNeural temp;
		if(num[0]==0){
			temp=new DynamicNeural(num[1]);
			temp.init_param();
		}else if(num[0]==1){
			temp=new DynamicNeural(num[2]);
			temp.init_param(num[1]);
		}else{
			temp=new DynamicNeural();
		}
		return temp;
	}
	
	//生成权重数组
	private void powerCreate() {
		this.power =new double [this.numberOfLayers][][];
		for(int i=0;i<this.numberOfLayers;i++)
			this.power[i]=new double[this.amount.get(i)][];
	}
	//获取神经网络权重
	public double[][][] getPower() {
		if(!powerCreateMark){
			powerCreate();
			this.powerCreateMark=true;
		}
		if(!powerDataMark){
			powerInput();
			this.powerDataMark=true;
			return this.power;
		}
		else
			return this.power;
	}
	public void powerInput() {
		for(int i=0;i<numberOfLayers;i++)
			for(int j=0;j<amount.get(i);j++)
				power[i][j]=cellGroup.get(i).get(j).getPowerArray();
	}
	//生成偏置数组
	private void biasCreate() {
		this.bias=new double[numberOfLayers][];
		for(int i=0;i<numberOfLayers;i++) this.bias[i]=new double[amount.get(i)];
	}
	//获取神经网络偏置
	public double[][] getBias(){
		if(!biasCreateMark){
			biasCreate();
			this.biasCreateMark=true;
		}
		if(!biasDataMark){
			biasInput();
			this.biasDataMark=true;
			return this.bias;
		}
		else
			return this.bias;
		
	}
	private void biasInput() {
		for(int i=0;i<numberOfLayers;i++)
			for(int j=0;j<amount.get(i);j++)
				bias[i][j]=cellGroup.get(i).get(j).getBias();
	}
	//设置神经元使用的函数
	public void setNeuralFun(int funindex){
		for(int i=0;i<numberOfLayers;i++)
			for(int j=0;j<cellGroup.get(i).size();j++)
				cellGroup.get(i).get(j).setfun(funindex);
	}
	//获取第index层的神经元个数
	public int getAmount(int index){
		if(index>=numberOfLayers) return 0;
		else return this.amount.get(index);
	}
	//获取层数
	public int getNumberOfLayers(){
		return this.numberOfLayers;
	}
	//设置阀值
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	//获取阀值
	public double getThreshold() {
		return threshold;
	}
	//数据阀值调整
	private double[] _Data(double[] data,double threshold){
		double[] _data=new double[data.length];
		for(int i=0;i<data.length;i++) _data[i]=data[i];
		for(int i=0;i<data.length;i++)
			if(_data[i]>=threshold) _data[i]=1.0;
			else _data[i]=0.0;
		return _data;
	}
	//梯度移动步幅
	public double getMovesize() {
		return this.movesize;
	}
	public void setMovesize(double movesize) {
		this.movesize = movesize;
	}
	public void setRandomMovesize(boolean movesizeRandom) {
		this.movesizeRandom = movesizeRandom;
	}
	public void setRandomMovesize(boolean movesizeRandom,double a,double b) {
		this.movesizeRandom = movesizeRandom;
		this.movesizeMIN=a;
		this.movesizeMAX=b;
	}
	public void setRandomMovesize(double a,double b) {
		this.movesizeMIN=a;
		this.movesizeMAX=b;
	}
	public void setMovesizeFun(boolean movesizeFun,int n) {
		this.movesizeFun=movesizeFun;
		this.movesizeFunIndex=n;
	}
	private double movesizeFun(int movesizeFunIndex,double... n) {
		return MathUsed.getMovesizeFun(movesizeFunIndex, n);
	}
	//对神经元处理
	/**************************************************************************/
	//添加index行n个神经元
	public void addNeural(int index,int n) {
		Random tool=new Random();
		if (index > 0 && index < numberOfLayers-1){
			for(int i=0;i<n;i++)
				cellGroup.get(index).add(_Neuralmaker(1,cellGroup.get(index-1).size(),cellGroup.get(index+1).size()));
			//System.out.println("添加成功！");
			for(int i=0;i<cellGroup.get(index-1).size();i++)
				for (int j=0;j<n;j++)
					cellGroup.get(index-1).get(i).addlink(tool.nextDouble());
			//System.out.println("更新成功！");
		}else if(index==0){
			for(int i=0;i<n;i++)
					cellGroup.get(index).add(_Neuralmaker(0,cellGroup.get(index+1).size()));
		}else if(index==numberOfLayers-1){
			for(int i=0;i<n;i++)
				cellGroup.get(index).add(_Neuralmaker(2));
			
			for(int i=0;i<amount.get(numberOfLayers-2);i++)
				for (int j=0;j<n;j++)
					cellGroup.get(numberOfLayers-2).get(i).addlink(tool.nextDouble());
			
			this._Error=new double[cellGroup.get(index).size()];
			this.output=new double[cellGroup.get(index).size()];
			this.numberOfOutputNeural=cellGroup.get(index).size();
		}
		
		this.amount.set(index, cellGroup.get(index).size());
		//权重相关标记
		powerCreateMark=false;
		powerDataMark=false;
		//偏置相关标记
		biasCreateMark=false;
		biasDataMark=false;
	}
	//删掉index行n个神经元
	public void delNeural(int index,int n) {
		if (index > 0 && index <= numberOfLayers-1){
			for(int i=0;i<n;i++)
				cellGroup.get(index).remove(cellGroup.get(index).size()-1);
			//System.out.println("删除阶段OK!");
			//更新上层数据
			for(int i=0;i<cellGroup.get(index-1).size();i++)
				for(int j=0;j<n;j++)
					cellGroup.get(index-1).get(i).dellink();
			//System.out.println("更新阶段OK!");
			if(index==numberOfLayers-1){
				this._Error=new double[cellGroup.get(index).size()];
				this.output=new double[cellGroup.get(index).size()];
				this.numberOfOutputNeural=cellGroup.get(index).size();
			}
		}else if(index==0){
			for(int i=0;i<n;i++)
				cellGroup.get(index).remove(cellGroup.get(index).size()-i);
		}
		this.amount.set(index, cellGroup.get(index).size());
		//权重相关标记
		powerCreateMark=false;
		powerDataMark=false;
		//偏置相关标记
		biasCreateMark=false;
		biasDataMark=false;
	}
	//添加1层神经元
	public void addLayer(int index,int n) {
		Random tool=new Random();
		if (index > 0 && index <= cellGroup.size()-1){
			if(cellGroup.get(index).size()<=n)
				for(int i=0;i<cellGroup.get(index-1).size();i++)
					for(int j=0;j<n-cellGroup.get(index).size();j++)
						cellGroup.get(index-1).get(i).addlink(tool.nextDouble());
			else
				for(int i=0;i<cellGroup.get(index-1).size();i++)
					for(int j=0;j<cellGroup.get(index).size()-n;j++)
						cellGroup.get(index-1).get(i).dellink();
			cellGroup.add(index,_Gloop(n, cellGroup.get(index-1).size(),cellGroup.get(index).size(),1 ));
		}else if(index==0){
			cellGroup.add(0,_Gloop(n,0,cellGroup.get(0).size(),0 ));
		}else if(index==cellGroup.size()){
			cellGroup.remove(index-1);
			if(index>1)
				cellGroup.add(index-1,_Gloop(this.amount.get(index-1),this.amount.get(index-2),n,1 ));
			else
				cellGroup.add(index-1,_Gloop(this.amount.get(index-1),0,n,0 ));
			cellGroup.add(index,_Gloop(n,0,0,2 ));
			this._Error=new double[cellGroup.get(index).size()];
			this.output=new double[cellGroup.get(index).size()];
			this.numberOfOutputNeural=cellGroup.get(index).size();
		}
		this.numberOfLayers++;
		this.amount.add(index, n);
		//权重相关标记
		powerCreateMark=false;
		powerDataMark=false;
		//偏置相关标记
		biasCreateMark=false;
		biasDataMark=false;
	}
	// 删除1层神经元
	public void delLayer(int index) {
		/////DEBUG
//		System.out.println("删除层测试：");
//		System.out.println("index="+index);
//		System.out.println();
		/////
		if (index > 0 && index < cellGroup.size()-1){
			int temp=cellGroup.get(index+1).size();
			this.cellGroup.remove(index);
			this.amount.remove(index);
			this.numberOfLayers--;
			this.cellGroup.remove(index);
			this.amount.remove(index);
			this.numberOfLayers--;
			//TODO
			addLayer(index, temp);
			/////-----
//			System.out.println("numberOfLayers="+numberOfLayers);
//			System.out.println("amount.size()="+amount.size());
			/////
		}else if(index==0){
			this.cellGroup.remove(0);
			this.amount.remove(index);
			this.numberOfLayers--;
		}else if(index==this.cellGroup.size()-1){
			int temp=this.cellGroup.get(index-1).size();
			this.cellGroup.remove(index);
			this.amount.remove(index);
			this.numberOfLayers--;
			/////------
//			System.out.println("amount.size()="+amount.size());
//			for(int i=0;i<amount.size();i++) System.out.print(amount.get(i)+"\t");
//			System.out.println();
			/////
			this.cellGroup.remove(index-1);
			this.amount.remove(index-1);
			this.numberOfLayers--;
			/////------
//			System.out.println("amount.size()="+amount.size());
//			for(int i=0;i<amount.size();i++) System.out.print(amount.get(i)+"\t");
//			System.out.println();
//			System.out.println("最后一层测试OK!");
			/////
			addLayer(index-1, temp);
			this._Error=new double[cellGroup.get(index-1).size()];
			this.output=new double[cellGroup.get(index-1).size()];
			this.numberOfOutputNeural=cellGroup.get(index-1).size();
		}
		//权重相关标记
		powerCreateMark=false;
		powerDataMark=false;
		//偏置相关标记
		biasCreateMark=false;
		biasDataMark=false;
	}
	//关闭某个神经元
	public boolean closeNeural(int index,int n){
		cellGroup.get(index).get(n).close();
		return true;
	}
	//关闭从n到m的神经元
	public boolean closeNeural(int index,int n,int m){
		if ((m-n)==cellGroup.get(index).size()) return false;
		for(int i=n;i<m;i++) cellGroup.get(index).get(i).close();
		return true;
	}
	//开启某个神经元
	public boolean openNeural(int index,int n){
		cellGroup.get(index).get(n).open();
		return true;
	}
	//开启从n到m的神经元
	public boolean openNeural(int index,int n,int m){
		if ((m-n)==cellGroup.get(index).size()) return false;
		for(int i=n;i<m;i++) cellGroup.get(index).get(i).open();
		return true;
	}
	//更新权重和偏置
	public DynamicNeural updata(DynamicNeural cell,double[] power,double bias){
		cell.setBias(bias);
		cell.setPower(power);
		return cell;
	}
	//更新权重
	public DynamicNeural updataPower(DynamicNeural cell,int index,double power) {
		cell.setPower(index, power);
		return cell;
	}
	//更新偏置
	public DynamicNeural updateBias(DynamicNeural cell,double bias){
		cell.setBias(bias);
		return cell;
	}
	//神经元间链接关闭
	public void closeNeuralLink(int m,int n,int index){
		this.cellGroup.get(m).get(n).closeLink(index);
	}
	//神经元间链接开启
	public void openNeuralLink(int m,int n,int index){
		this.cellGroup.get(m).get(n).openLink(index);
	}
	/**************************************************************************/
	//mark取消方法
	public boolean isPowerCreateMark() {
		return powerCreateMark;
	}
	public void setPowerCreateMark(boolean powerCreateMark) {
		this.powerCreateMark = powerCreateMark;
	}
	public boolean isPowerDataMark() {
		return powerDataMark;
	}
	public void setPowerDataMark(boolean powerDataMark) {
		this.powerDataMark = powerDataMark;
	}
	public boolean isBiasCreateMark() {
		return biasCreateMark;
	}
	public void setBiasCreateMark(boolean biasCreateMark) {
		this.biasCreateMark = biasCreateMark;
	}
	public boolean isBiasDataMark() {
		return biasDataMark;
	}
	public void setBiasDataMark(boolean biasDataMark) {
		this.biasDataMark = biasDataMark;
	}
	public void markAllFalse(){
		this.powerCreateMark=false;
		this.powerDataMark=false;
		
		this.biasCreateMark=false;
		this.biasDataMark=false;
	}
	public void markDataFalse(){
		this.powerDataMark=false;
		this.biasDataMark=false;
	}
	public void markCreatFalse(){
		this.powerCreateMark=false;
		this.biasCreateMark=false;
	}
	/**************************************************************************/
	//训练网络
	/**************************************************************************/
	private int trainnext=0;
	
	int trainNumber;//训练集数量
	private double[][] trainingdata;
	private double[][] calibrationdata;
	private double[] _Error;
	/**************************************************************************/
	//置训练集
	public void trainset(double[][] trainingdata,double[][] calibrationdata){
		this.trainNumber=trainingdata.length;
		this.trainingdata=trainingdata;
		this.calibrationdata=calibrationdata;
		this.trainnext=0;
	}
	//获取神经网络输出
	public double[] getNetworksOutput(double[] trainingdata){
		this.output=new double[this.numberOfOutputNeural];
		//正向传播
		_networksForwardPropagation(trainingdata);
		for(int i=0;i<this.numberOfOutputNeural;i++) this.output[i]=cellGroup.get(numberOfLayers-1).get(i).output();
		return this.output;
	}
	public double[] getNetworksOutput(){
		return this.output;
	}
	
	//网络正向传播!!!
	private void _networksForwardPropagation(double[] trainingdata) {
		//输入层载入
		for(int n=0;n<amount.get(0);n++)
			this.cellGroup.get(0).get(n).input(trainingdata[n]);
		//执行计算
		for(int i=0;i<numberOfLayers-1;i++)
        	for(int j=0;j<amount.get(i+1);j++)
        		if(this.cellGroup.get(i+1).get(j).checkNeural())
    				this.cellGroup.get(i+1).get(j).input(cellGroup.get(i),j);
	}
	public void trainNetworks(double[] trainingdata, double[] calibrationdata) {
		getNetworksOutput(trainingdata);
		for(int i=0;i<this.numberOfOutputNeural;i++)
			this._Error[i]=cellGroup.get(numberOfLayers-1).get(i).diffOutput()*(calibrationdata[i]-this.output[i]);
		//反向传播
		_networksBackPropagation(_Error);
		this.powerDataMark=false;
	}
	//网络反向传播!!!
	private void _networksBackPropagation(double[] _Error) {
		double[] backError =new double[amount.get(this.numberOfLayers-1)];
		double[] temp=_Error;
		double[] temppow;
		double sum;
		double num;
		
		if(this.movesizeRandom){
			this.movesize=new MathUsed().randomDouble(movesizeMIN,movesizeMAX);
		}else if(this.movesizeFun){
			this.movesize=movesizeFun(this.movesizeFunIndex,_Error);
		}
		for(int i=this.numberOfLayers-1;i>0;i--){
			backError=new double [this.amount.get(i-1)];
			for(int j=0;j<this.amount.get(i-1);j++){
				if(!cellGroup.get(i-1).get(j).checkNeural()) continue;
				sum=0;
				temppow=cellGroup.get(i-1).get(j).getPowerArray();
				for(int k=0;k<temp.length;k++)
					sum+=temp[k]*temppow[k];
				backError[j]=cellGroup.get(i-1).get(j).diffOutput()*sum;
				cellGroup.get(i-1).get(j).setBias(cellGroup.get(i-1).get(j).getBias()+movesize*backError[j]);
				num=movesize*backError[j];
	            for (int k=0;k<this.amount.get(i); k++){
	            	if(!cellGroup.get(i-1).get(j).checkLink(k)) continue;
	            	cellGroup.get(i-1).get(j).setPower(k, cellGroup.get(i-1).get(j).getPower(k)+num*cellGroup.get(i-1).get(j).output(k));
	            	//System.out.println(cellGroup.get(i-1).get(j).output(k));
	            }
	            //System.out.println();
			}
			temp=new double[backError.length];
			for(int x=0;x<backError.length;x++) temp[x]=backError[x];
		}
	}
	public boolean nextTrain() {
		if(trainnext<trainNumber){
			trainNetworks(trainingdata[trainnext],calibrationdata[trainnext]);
			this.trainnext++;
			return true;
			}
		return false;
	}
	public int trainNetworksAll() {
		for(int i=0;i<trainNumber;i++)
			trainNetworks(trainingdata[i],calibrationdata[i]);
		return 1;
	}
	/**************************************************************************/
	private int testnext=0;
	private int testNumber;//测试集数量
	private double[][] testdata;
	private double[][] validation;
	/**************************************************************************/
	//置测试集
	public void testset(double[][] testdata,double[][] validation){
		this.testNumber=testdata.length;
		this.testdata=testdata;
		this.validation=validation;
		this.testnext=0;
	}
	//测试神经网络
	public boolean testNetworks(double[] data,double[] result){
		double[] temp=getNetworksOutput(data);
		return _Check(_Data(temp,this.threshold),result);
	}
	//与置测试集有关
	public boolean nextTest() {
		if(testnext<testNumber){
			testNetworks(testdata[testnext],validation[testnext]);
			this.testnext++;
			return true;
		}
		return false;
	}

	//判断两部分数据是否想等
	private boolean _Check(double[] data1,double[] data2){
		if(data1.length!=data2.length) return false;
		for (int i=0;i<data1.length;i++)
			if((int)data1[i]!=(int)data2[i]) return false;
		return true;
	}
	//正确率提取
	public double 	networksAccuracy(double[][] testdata,double[][] validation){
		int right=0;
		int testdataamount=testdata.length;
		double rate;
		for(int i=0;i<testdataamount;i++)
			if(testNetworks(testdata[i],validation[i])) right++;
		rate=(double)right/(double)testdataamount;
		return rate;
	}
	/**************************************************************************/
	public double[] getNetworksResule(){
		return _Data(this.output,this.threshold);
	}
	/**************************************************************************/
}
