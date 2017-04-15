/* 静态神经网络类
 * 版本：0.0.0.1
 * 开发时间：2017.3.30
 * 最后一次更改：2017.4.14
 * 最后一次更改内容：无
 */
package saie.neuralnetworks.net.staticBPnet_cpu;

import saie.neuralnetworks.net.MathUsed;

public class StaticNetworks {
	
	private int[] amount;
	private int numberOfLayers=1;
	private StaticNeural[][] cell;
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
	//输出层输出
	private double[] output;
	private double threshold=0.5;
	//权重相关标记
	private boolean powerCreateMark=false;
	private boolean powerDataMark=false;
	//偏置相关标记
	private boolean biasCreateMark=false;
	private boolean biasDataMark=false;
	
	//构造函数-1 创造神经网络
	public StaticNetworks(int... amount) {
		this.amount=amount;
		this.numberOfLayers=amount.length;
		this.numberOfOutputNeural=amount[numberOfLayers-1];
		this.output = new double[amount[numberOfLayers-1]];
		this._Error=new double[amount[numberOfLayers-1]];

		createNetworks(amount,0);
		
	}
	public StaticNetworks(int amount[],int n) {
		this.amount=amount;
		this.numberOfLayers=amount.length;
		this.numberOfOutputNeural=amount[numberOfLayers-1];
		this.output = new double[amount[numberOfLayers-1]];
		this._Error=new double[amount[numberOfLayers-1]];
		
		switch (n){
			case 0:noInitCreateNetworks(amount);break;
			case 1:createNetworks(amount,0);break;
			default:createNetworks(amount,0);
		}
	}
	public StaticNetworks(double w_min,double w_max,double b_min,double b_max,int... amount) {
		this.amount=amount;
		this.numberOfLayers=amount.length;
		this.numberOfOutputNeural=amount[numberOfLayers-1];
		this.output = new double[amount[numberOfLayers-1]];
		this._Error=new double[amount[numberOfLayers-1]];
		
		createNetworks(w_min,w_max,b_min,b_max,amount);
	}
	//构造函数-2 载入神经网络
	public StaticNetworks(double[][][] power,double[][] bias) {
		this.power=power;
		this.bias=bias;
//		this.powerDataMark=true;
//		this.powerDataMark=true;
//		this.biasCreateMark=true;
//		this.biasDataMark=true;
		
		this.numberOfLayers=power.length;
		//加载神经网络
		for(int i=0;i<numberOfLayers;i++)
			this.amount[i]=power[i].length;
		
		this.numberOfOutputNeural=amount[numberOfLayers-1];
		this.output = new double[amount[numberOfLayers-1]];
		this._Error=new double[amount[numberOfLayers-1]];
		createNetworks(amount,0);
		for(int i=0;i<amount.length;i++)
			for(int j=0;j<power[i].length;j++){
				cell[i][j].setBias(this.bias[i][j]);
				for(int k=0;k<power[i][j].length;k++)
					cell[i][j].setPower(k, power[i][j][k]);
				}
	}
	
	//创造神经网络
	private void createNetworks(int[] amount,int n) {
			this.cell=new StaticNeural[this.numberOfLayers][];
			// 给每层神经元初始化
			//输入层
			this.cell[0]=new StaticNeural[amount[0]];
			for(int j=0;j<amount[0];j++){
				this.cell[0][j]=new StaticNeural(amount[1],0);
			}
			//隐藏层
			for(int i=1;i<this.numberOfLayers-1;i++){
				this.cell[i]=new StaticNeural[amount[i]];
					for(int j=0;j<amount[i];j++){
						this.cell[i][j]=new StaticNeural(amount[i+1]);
						this.cell[i][j].init_param(amount[i-1]);
					}
			}
			//输出层
			this.cell[this.numberOfLayers-1]=new StaticNeural[amount[this.numberOfLayers-1]];
			for(int j=0;j<amount[this.numberOfLayers-1];j++)
				this.cell[numberOfLayers-1][j]=new StaticNeural();
	}
	private void createNetworks(double w_min,double w_max,double b_min,double b_max,int[] amount){
		this.cell=new StaticNeural[this.numberOfLayers][];
		// 给每层神经元初始化
		for(int i=0;i<this.numberOfLayers-1;i++){
			this.cell[i]=new StaticNeural[amount[i]];
				for(int j=0;j<amount[i];j++)
					this.cell[i][j]=new StaticNeural(w_min,w_max,b_min,b_max,amount[i+1]);		
		}
		this.cell[this.numberOfLayers-1]=new StaticNeural[amount[this.numberOfLayers-1]];
		for(int j=0;j<amount[this.numberOfLayers-1];j++)
			this.cell[numberOfLayers-1][j]=new StaticNeural();
	}
	private void noInitCreateNetworks(int[] amount) {
		this.cell=new StaticNeural[this.numberOfLayers][];
		for(int i=0;i<this.numberOfLayers;i++){
			this.cell[i]=new StaticNeural[amount[i]];
		}
	}
	//生成权重数组
	private void powerCreate() {
		this.power =new double [this.numberOfLayers][][];
		for(int i=0;i<this.numberOfLayers;i++)
			this.power[i]=new double[this.amount[i]][];
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
			for(int j=0;j<amount[i];j++)
				power[i][j]=cell[i][j].getPower();
	}
	//生成偏置数组
	private void biasCreate() {
		this.bias=new double[numberOfLayers][];
		for(int i=0;i<numberOfLayers;i++) this.bias[i]=new double[amount[i]];
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
			for(int j=0;j<amount[i];j++)
				bias[i][j]=cell[i][j].getBias();
	}
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
	//设置神经元使用的函数
	public void setNeuralFun(int funindex){
		for(int i=0;i<numberOfLayers;i++)
			for(int j=0;j<cell[i].length;j++)
				cell[i][j].setfun(funindex);
	}
	//获取第index层的神经元个数
	public int getAmount(int index){
		if(index>=numberOfLayers) return 0;
		else return this.amount[index];
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
	//关闭某个神经元
	public boolean closeNeural(int index,int n){
		cell[index][n].close();
		return true;
	}
	//关闭从n到m的神经元
	public boolean closeNeural(int index,int n,int m){
		if ((m-n)==cell[index].length) return false;
		for(int i=n;i<m;i++) cell[index][i].close();
		return true;
	}
	//开启某个神经元
	public boolean openNeural(int index,int n){
		cell[index][n].open();
		return true;
	}
	//开启从n到m的神经元
	public boolean openNeural(int index,int n,int m){
		if ((m-n)==cell[index].length) return false;
		for(int i=n;i<m;i++) cell[index][i].open();
		return true;
	}
	//更新权重和偏置
	public StaticNeural updata(StaticNeural cell,double[] power,double bias){
		cell.setBias(bias);
		cell.setPower(power);
		return cell;
	}
	//更新权重
	public StaticNeural updataPower(StaticNeural cell,int index,double power) {
		cell.setPower(index, power);
		return cell;
	}
	//更新偏置
	public StaticNeural updateBias(StaticNeural cell,double bias){
		cell.setBias(bias);
		return cell;
	}
	//神经元间链接关闭
	public void closeNeuralLink(int m,int n,int index){
		this.cell[m][n].closeLink(index);
	}
	//神经元间链接开启
	public void openNeuralLink(int m,int n,int index){
		this.cell[m][n].openLink(index);
	}
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
		for(int i=0;i<this.numberOfOutputNeural;i++) this.output[i]=cell[numberOfLayers-1][i].output();
		return this.output;
	}
	public double[] getNetworksOutput(){
		return this.output;
	}
	
	//网络正向传播!!!
	private void _networksForwardPropagation(double[] trainingdata) {
		//输入层载入
		for(int n=0;n<amount[0];n++)
			this.cell[0][n].input(trainingdata[n]);
		//执行计算
		for(int i=0;i<numberOfLayers-1;i++)
        	for(int j=0;j<amount[i+1];j++)
        		if(this.cell[i+1][j].checkNeural())
    				this.cell[i+1][j].input(cell[i],j);
	}
	public void trainNetworks(double[] trainingdata, double[] calibrationdata) {
		getNetworksOutput(trainingdata);
		for(int i=0;i<this.numberOfOutputNeural;i++)
			this._Error[i]=cell[numberOfLayers-1][i].diffOutput()*(calibrationdata[i]-this.output[i]);
		//反向传播
		_networksBackPropagation(_Error);
		this.powerDataMark=false;
	}
	//网络反向传播!!!
	private void _networksBackPropagation(double[] _Error) {
		double[] backError =new double[amount[this.numberOfLayers-1]];
		double[] temp=_Error;
		double[] temppow;
		double sum;
		double num;
		// 判断是否使用“步长”的随机或函数
		if(this.movesizeRandom){
			this.movesize=new MathUsed().randomDouble(movesizeMIN,movesizeMAX);
		}else if(this.movesizeFun){
			this.movesize=movesizeFun(this.movesizeFunIndex,_Error);
		}
		
		for(int i=this.numberOfLayers-1;i>0;i--){
			backError=new double [this.amount[i-1]];
			for(int j=0;j<this.amount[i-1];j++){
				if(!cell[i-1][j].checkNeural()) continue;
				sum=0;
				temppow=cell[i-1][j].getPower();
				for(int k=0;k<temp.length;k++)
					sum+=temp[k]*temppow[k];
				backError[j]=cell[i-1][j].diffOutput()*sum;
				cell[i-1][j].setBias(cell[i-1][j].getBias()+movesize*backError[j]);
				num=movesize*backError[j];
	            for (int k=0;k<this.amount[i]; k++){
	            	if(!cell[i-1][j].checkLink(k)) continue;
	            	cell[i-1][j].setPower(k, cell[i-1][j].getPower(k)+num*cell[i-1][j].output(k));
	            	//System.out.println(cell[i-1][j].output(k));
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