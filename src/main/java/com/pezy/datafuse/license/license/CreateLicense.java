package com.pezy.datafuse.license.license;

import com.pezy.datafuse.license.license3j.CheckLicense;
import com.verhas.licensor.License;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CreateLicense {
	
	private static Logger logger = LoggerFactory.getLogger(CheckLicense.class);

	/**
	 * 
	 * @param fileCreate   加密后生成的文件
	 * @param inputFile    要被加密的文件
	 * @param secringFile  私钥文件
	 */
	public static void creat(String fileCreate,String inputFile,String secringFile){
		  File licenseFile = new File(fileCreate);
		  OutputStream os=null;
          if (!licenseFile.exists()) {
          
			try {
                //license 文件生成
				os = new FileOutputStream("config/demo.licenses");
			
              os.write(new License()
                   // license 的原文
                  .setLicense(new File(inputFile))
                   // 私钥与之前生成密钥时产生的USER-ID
                  .loadKey(secringFile,"fenggang <fg191392@163.com>")
                  // 锟斤拷锟斤拷锟皆渴憋拷锟斤拷锟斤拷锟斤拷锟斤拷
                  .encodeLicense("fg19920229").getBytes("utf-8"));
             os.flush();
              logger.info("success");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if(os!=null){
		              try {
						os.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
          }else {
        	  logger.info("is hived");
          }
	}
	public static void main(String[] args) {
		creat("config/demo.license","config/license-plain.txt","config/secring.gpg");
	}
}
