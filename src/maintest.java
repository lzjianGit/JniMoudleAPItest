import com.uhf.api.cls.Reader;
import com.uhf.api.cls.Reader.*;
 


public class maintest {

	/**
	 * @param args
	 */
	
	int AntCount=4;
	String ReaderAddr="192.168.1.100";
	Reader Jreader;
	public maintest()
	{
		Jreader=new Reader();
	}
	
	public void  testtran()
	{
		byte[] hex=new byte[]{(byte) 0xA2,(byte) 0xC8,(byte) 0xD4,(byte) 0xE5};
		int len=4;
		char[] str=new char[4*2];
	 	Jreader.Hex2Str(hex, len, str);
		String hstr = "";
		for(int i=0;i<8;i++)
		hstr+=(char)str[i];
		System.out.println("test tohexstr:"+hstr);
		
		String buf="0011110000001111";
	 
		byte[] binarybuf=new byte[2];
		String buf2="abcdef08";
		byte[] hexbuf=new byte[4];
		Jreader.Str2Binary(buf, buf.length(), binarybuf);
		System.out.println("test binary:");
		for(int i=0;i<binarybuf.length;i++)
			System.out.println(binarybuf[i]);
		
		Jreader.Str2Hex(buf2, 8, hexbuf);
		System.out.println("test hex:");
		for(int i=0;i<hexbuf.length;i++)
			System.out.println(hexbuf[i]);
	}
	
	public void testreadandwrite()
	{
		/*
		 * READER_ERR WriteTagData(int ant,char bank,int address, byte[] data, int datalen, byte[] accesspasswd,short timeout);
		 * ant 操作的单天线
		 * bank 表示区域 0表示保留区 1表示epc区 2表示tid区 3表示user区
		 * address 表示地址块， 注意epc区从第二块开始
		 * data 写的数据
		 * datalen 表示写的数据长度
		 * accesspwd 表示密码，默认"00000000" 8个十六进制字符
		 * timeout 操作超时时间
		 */
		
		String pwd="00000000";
		byte[] data=new byte[]{0x00,0x11,0x22,0x33,0x44,0x55,0x66,0x77,(byte) 0x88,(byte) 0x99,(byte) 0xaa,(byte) 0xbb};
		  byte[] pwdb=new byte[4];
	      Jreader.Str2Hex(pwd, pwd.length(), pwdb);
		//写数据
		READER_ERR er=Jreader.WriteTagData(1, (char)1, 2, data, 6, pwdb, (short)5000);
		
		byte[] datar=new byte[12];
		//读数据
		/*
		 * READER_ERR GetTagData(int ant,char bank, int address, int blkcnt,byte[] data, byte[] accesspasswd, short timeout);
		 * ant 操作的单天线
		 * bank 表示区域 0表示保留区 1表示epc区 2表示tid区 3表示user区
		 * address 表示地址块， 注意epc区从第二块开始
		 * blkcnt 表示读块数
		 * data 存放数据的字节，应该不小于blkcnt*2
		 * accesspwd 表示密码，默认"00000000" 8个十六进制字符
		 * timeout 操作超时时间
		 */
		er=Jreader.GetTagData(1, (char)1, 2, 6, datar, null, (short)5000);
		String str1="";
	
		for(int i=0;i<12;i++)
		{
			str1+=Integer.toHexString(datar[i]&0xff);
		}
		System.out.println(er.toString()+" "+str1.toUpperCase());
		
		byte[] data2=new byte[]{(byte) 0xFF,0x01,0x22,0x03,0x44,0x05,0x66,0x07,(byte) 0x88,(byte) 0x09,(byte) 0xaa,(byte) 0x0b};

		er=Jreader.WriteTagEpcEx(1, data2, 12, null, (short)5000);
		
		er=Jreader.GetTagData(1, (char)1, 2, 6, datar, null, (short)5000);
		
		str1="";
		
		for(int i=0;i<12;i++)
		{
			str1+=Integer.toHexString(datar[i]&0xff);
		}
		System.out.println(er.toString()+" "+str1.toUpperCase());

	}
	
	public void testblockop()
	{
		 String pwd="11000000";
		  byte[] data=new byte[4];
	      Jreader.Str2Hex(pwd, pwd.length(), data);
		//擦除块
		READER_ERR er=Jreader.BlockErase(1, (char)1, 2, 12, data, (short)1000);
		
		//永久锁块
		 Jreader.BlockPermaLock(1, 1, 2, 6, new byte[]{(byte) 0xff,(byte) 0xff}, data, (short)1000);

	
	}
	
	public void testinitreader()
	{
		//创建读写器
		/* 根据天线口连接读写器
		 *   src 是地址 ip地址或者串口号
		 *   rtype 是天线口数，4口天线传入4
		 *   返回类型：READER_ERR ,MT_OK_ERR表示正常，其他表示错误
		 */
		//READER_ERR er=Jreader.InitReader("com3",Reader_Type.MODULE_FOUR_ANTS);
		READER_ERR er=Jreader.InitReader_Notype(ReaderAddr,4);
		System.out.println(er.toString());
		
		
		/*
		 * 构建天线组功率：AntPowerConf  
		 * 成员：
		 * AntPower数组
		 * antcnt表示天线个数
		 * AntPower类型 
		 * antid 天线号
		 * readPower 读功率
		 * writePower 写功率 
		 */
		
		AntPowerConf apcf=Jreader.new AntPowerConf();
		apcf.antcnt=AntCount;
		for(int i=0;i<apcf.antcnt;i++)
		{
			AntPower jaap=Jreader.new AntPower();
			jaap.antid=i+1;
			jaap.readPower=3000;
			jaap.writePower=3000;
			apcf.Powers[i]=jaap; 
		}
		AntPowerConf apcf2=Jreader.new AntPowerConf();
		er=Jreader.ParamSet( Mtr_Param.MTR_PARAM_RF_ANTPOWER, apcf);
		er=Jreader.ParamGet( Mtr_Param.MTR_PARAM_RF_ANTPOWER, apcf2);
		for(int i=0;i<apcf2.antcnt;i++)
		{
			System.out.print("antid:"+apcf2.Powers[i].antid);
			System.out.print(" rp:"+apcf2.Powers[i].readPower);
			System.out.print(" wp:"+apcf2.Powers[i].writePower);
			System.out.println();
		}
		
		Inv_Potls_ST ipst=Jreader.new Inv_Potls_ST();
		ipst.potlcnt=1;
		ipst.potls=new Inv_Potl[1];
		for(int i=0;i<ipst.potlcnt;i++)
		{
			Inv_Potl ipl=Jreader.new Inv_Potl();
			ipl.weight=30;
			ipl.potl=SL_TagProtocol.SL_TAG_PROTOCOL_GEN2;
			ipst.potls[0]=ipl;
		}
		
	 
		er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_TAG_INVPOTL, ipst);
		
		/*
		 * 设置是否检查天线
		 * 当参数值传入1的时候表示要检查，0表示不检查
		 */
		er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_READER_IS_CHK_ANT, 0);
	}
	
	public void testdataonreader()
	{
		byte[] data3=new byte[100];
		READER_ERR er=Jreader.ReadDataOnReader(0, data3, 100);
		 er=Jreader.SaveDataOnReader(0, data3, 100);
		
		//擦除读写器上数据
		 er=Jreader.EraseDataOnReader();
	}
	
	public void testsetip()
	{
		Reader_Ip rip=Jreader.new Reader_Ip();
		/*
		rip.ip=new byte[]{'1','9','2','.','1','6','8','.','1','.','1','0','1'};
		rip.mask=new byte[]{'2','5','5','.','2','5','5','.','2','5','5','.','0'};
		rip.gateway=new byte[]{'1','9','2','.','1','6','8','.','1','.','2','5','4'};
		//*/
		
		rip.ip="192.168.1.100".getBytes();
		rip.mask="255.255.255.0".getBytes();
		rip.gateway="192.168.1.1".getBytes();
		//*/
		
		READER_ERR er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_READER_IP, rip);
		
	}
	public void testrparams()
	{
		HoptableData_ST hdst=Jreader.new HoptableData_ST();
		hdst.lenhtb=5;
		hdst.htb[0]=915250;
		hdst.htb[1]=916750;
		hdst.htb[2]=917250;
		hdst.htb[3]=925750;
		hdst.htb[4]=926750;
		READER_ERR er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_FREQUENCY_HOPTABLE,hdst);
		
		HoptableData_ST hdst2=Jreader.new HoptableData_ST();
		er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_FREQUENCY_HOPTABLE, hdst2);
		for(int i=0;i<hdst2.lenhtb;i++)
		{
			System.out.print("htb:"+i);
			System.out.println(" "+(hdst2.htb[i]));
		}
		
		//
		Region_Conf rcf1=Region_Conf.RG_NA;
		Region_Conf[] rcf2=new Region_Conf[1];
	
		er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_FREQUENCY_REGION,rcf1);
		er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_FREQUENCY_REGION,rcf2);
		
		int[] val1=new int[]{250};
		int[] val2=new int[]{-1};
		er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_BLF, val1);
		er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_POTL_GEN2_BLF, val2);

		val1[0]=496;
		er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_MAXEPCLEN, val1);
		er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_POTL_GEN2_MAXEPCLEN, val2);
		
		val1[0]=10;
		er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_Q, val1);
		er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_POTL_GEN2_Q, val2);
		
		val1[0]=2;
		er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION, val1);
		er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION, val2);
		
		val1[0]=2;
		er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_TAGENCODING, val1);
		er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_POTL_GEN2_TAGENCODING, val2);
		
		val1[0]=1;
		er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_TARGET, val1);
		er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_POTL_GEN2_TARGET, val2);
		
		val1[0]=3;
		er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_TARI, val1);
		er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_POTL_GEN2_TARI, val2);
		
		val1[0]=1;
		er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_WRITEMODE, val1);
		er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_POTL_GEN2_WRITEMODE, val2);
		
		val1[0]=2;
		er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_POTL_ISO180006B_BLF, val1);
		er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_POTL_ISO180006B_BLF, val2);
		
		val1[0]=1;
		er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_POTL_ISO180006B_DELIMITER, val1);
		er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_POTL_ISO180006B_DELIMITER, val2);
		
		val1[0]=2;
		er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_POTL_ISO180006B_MODULATION_DEPTH, val1);
		er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_POTL_ISO180006B_MODULATION_DEPTH, val2);
		
		//er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_POTL_SUPPORTEDPROTOCOLS, val1);
		//er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_POTL_SUPPORTEDPROTOCOLS, val2);
		
		val1[0]=1;
		val2[0]=-1;
		er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_POWERSAVE_MODE, val1);
		er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_POWERSAVE_MODE, val2);

		//er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_READER_AVAILABLE_ANTPORTS, val1);
		er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_READER_AVAILABLE_ANTPORTS, val2);
		
		ConnAnts_ST cast=Jreader.new ConnAnts_ST();
		//er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_READER_CONN_ANTS, val1);
		er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_READER_CONN_ANTS, cast);

		
		Reader_Ip rip2=Jreader.new Reader_Ip();
		er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_READER_IP, rip2);
		System.out.print("ip:"+rip2.ip.length+" ");
		System.out.println(new String(rip2.ip));
		System.out.println(new String(rip2.mask));
		System.out.println(new String(rip2.gateway));
		
		val1[0]=1;
		er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_READER_IS_CHK_ANT, val1);
		er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_READER_IS_CHK_ANT, val2);
		
		
		//er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_READER_VERSION, val1);
		//er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_READER_VERSION, val2);
		
		AntPowerConf apcf=Jreader.new AntPowerConf();
		apcf.antcnt=1;
		for(int i=0;i<apcf.antcnt;i++)
		{
			AntPower jaap=Jreader.new AntPower();
			jaap.antid=i+1;
			jaap.readPower=2800;
			jaap.writePower=2750;
			apcf.Powers[i]=jaap; 
		}
		AntPowerConf apcf2=Jreader.new AntPowerConf();
		er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_RF_ANTPOWER, apcf);
		er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_RF_ANTPOWER, apcf2);
		for(int i=0;i<apcf2.antcnt;i++)
		{
			System.out.print("antid:"+apcf2.Powers[i].antid);
			System.out.print(" rp:"+apcf2.Powers[i].readPower);
			System.out.print(" wp:"+apcf2.Powers[i].writePower);
			System.out.println();
		}
		
		val1[0]=100;
		er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_RF_HOPTIME, val1);
		er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_RF_HOPTIME, val2);
		
		val1[0]=1;
		er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_RF_LBT_ENABLE, val1);
		er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_RF_LBT_ENABLE, val2);
		
		
		//er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_RF_MAXPOWER, val1);
		short[] valso=new short[1];
		er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_RF_MAXPOWER, valso);
		System.out.println("max:"+valso[0]);
		
		//er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_RF_MINPOWER, val1);
		er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_RF_MINPOWER, valso);
		
		//er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_RF_SUPPORTEDREGIONS, val1);
		//er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_RF_SUPPORTEDREGIONS, val2);
		
		//er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_RF_TEMPERATURE, val1);
		er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_RF_TEMPERATURE, val2);
		
		EmbededData_ST edst = Jreader.new EmbededData_ST();
		edst.startaddr=0;
		edst.bank=2;
		//bytecnt=0 取消嵌入数据
		edst.bytecnt=2;
		edst.accesspwd=null;
		er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_TAG_EMBEDEDDATA, edst);
		
		EmbededData_ST edst2 = Jreader.new EmbededData_ST();
		er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_TAG_EMBEDEDDATA, edst2);
		
		er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_TAG_EMBEDEDDATA, null);
		
		EmbededSecureRead_ST esrst=Jreader.new EmbededSecureRead_ST();
		esrst.accesspwd=1280;
		esrst.address=2;
		esrst.ApIndexBitsNumInEpc=1;
		esrst.ApIndexStartBitsInEpc=3;
		esrst.bank=1;
		//blkcnt =0 取消。
		esrst.blkcnt=2;
		esrst.pwdtype=1;
		esrst.tagtype=2;
		EmbededSecureRead_ST esrst2=Jreader.new EmbededSecureRead_ST();
		er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_TAG_EMDSECUREREAD, esrst);
		er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_TAG_EMDSECUREREAD, esrst2);
		er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_TAG_EMDSECUREREAD, null);
		
		TagFilter_ST tfst=Jreader.new TagFilter_ST();
		tfst.bank=1;
		tfst.fdata=new byte[]{(byte) 0xE2,(byte) 0x00};
		//flen 0 为取消过滤
		tfst.flen=2;
		tfst.isInvert=0;
		tfst.startaddr=2;
		TagFilter_ST tfst2=Jreader.new TagFilter_ST();
		
		er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_TAG_FILTER, tfst);
		er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_TAG_FILTER, tfst2);
		er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_TAG_FILTER, null);
		
		Inv_Potls_ST ipst=Jreader.new Inv_Potls_ST();
		ipst.potlcnt=1;
		ipst.potls=new Inv_Potl[1];
		for(int i=0;i<ipst.potlcnt;i++)
		{
			Inv_Potl ipl=Jreader.new Inv_Potl();
			ipl.weight=30;
			ipl.potl=SL_TagProtocol.SL_TAG_PROTOCOL_GEN2;
			ipst.potls[0]=ipl;
		}
		
		Inv_Potls_ST ipst2=Jreader.new Inv_Potls_ST();
		er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_TAG_INVPOTL, ipst);
		er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_TAG_INVPOTL, ipst2);
		for(int i=0;i<ipst2.potlcnt;i++)
		System.out.println(ipst2.potls[i].potl);
		
		val1[0]=1;
		val2[0]=0;
		er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_TAG_SEARCH_MODE, val1);
		er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_TAG_SEARCH_MODE, val2);
		
		val1[0]=1;
		val2[0]=0;
		er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_TAGDATA_RECORDHIGHESTRSSI, val1);
		er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_TAGDATA_RECORDHIGHESTRSSI, val2);

		val1[0]=1;
		val2[0]=0;
		er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_TAGDATA_UNIQUEBYANT, val1);
		er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_TAGDATA_UNIQUEBYANT, val2);

		val1[0]=1;
		val2[0]=0;
		er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_TAGDATA_UNIQUEBYEMDDATA, val1);
		er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_TAGDATA_UNIQUEBYEMDDATA, val2);
		
		val1[0]=300;
		val2[0]=0;
		er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_TRANS_TIMEOUT, val1);
		er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_TRANS_TIMEOUT, val2);
		
		val1[0]=1;
		val2[0]=0;
		er=Jreader.ParamSet(Mtr_Param.MTR_PARAM_TRANSMIT_MODE, val1);
		er=Jreader.ParamGet(Mtr_Param.MTR_PARAM_TRANSMIT_MODE, val2);
	}
	
	public static void main(String[] args) {
		//TODO Auto-generated method stub
		System.out.println("测试开始");
		maintest mt=new maintest();
        //测试初始化
	    mt.testinitreader();
	    //测试转换
	    //mt.testtran();
	    //测试参数
	    //mt.testrparams();
	    //测试块操作
	    //mt.testblockop();
	    //测试读写器内部数据
	    //mt.testdataonreader();
	    //测试读写标签
	     mt.testreadandwrite();
	    //测试改ip地址
	    //mt.testsetip(); 
		//关闭读写器
		mt.Jreader.CloseReader();
		System.out.println("测试结束");
	}

}
