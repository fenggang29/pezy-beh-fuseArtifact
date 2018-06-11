package com.pezy.datafuse.license.license3j;

import com.pezy.datafuse.license.entity.ResultEnity;
import com.verhas.licensor.License;
import net.sf.json.JSONObject;
import org.bouncycastle.openpgp.PGPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CheckLicense {
    private String licensePath="/opt/beh/core/spark/config";
    private static final String pubringFileName = "pubring.gpg";
    private static final String licenseFileName = "demo.licenses";

    private static Logger logger = LoggerFactory.getLogger(CheckLicense.class);

    public Map<String, String> check() {
        String path = new File(licensePath).getAbsolutePath();
        License license = new License();
        Map<String, String> map = new HashMap<String, String>();
        try {

            if (license.loadKeyRing(path + "/" + pubringFileName, null)
                    .setLicenseEncodedFromFile(path + "/" + licenseFileName).isVerified()) {
                logger.info(license.getFeature("edition") + "--" + license.getFeature("valid-until") + "");
                map.put("valid-until", license.getFeature("valid-until"));
                map.put("edition", license.getFeature("edition"));
                map.put("mac", license.getFeature("mac"));
                System.out.print(map);
                return map;
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.info("文件获取错误");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (PGPException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    //缓存1天
    public ResultEnity checkResult() {
        ResultEnity result = new ResultEnity();
        JSONObject object = new JSONObject();
        String selfMac = LOCALMAC.GetAddressMac();
        object.put("selfMac", selfMac);
        // task to run goes here
        Map<String, String> map = check();
        if (map == null) {
            logger.info("license验证失败!配置文件缺失！");
            result.setErrMsg("license验证失败!!配置文件缺失！");
            result.setResult(false);
            result.setExtend(object);

            return result;
        } else {
            String dateUtil = map.get("valid-until");
            if (dateUtil == null) {
                logger.info("license没有设置有效期");
                result.setResult(true);
                return result;
            }
            boolean timeResult = compare_date(parseDate(dateUtil), new Date());
            if (!timeResult) {
                logger.info("license无效已过期");
                result.setErrMsg("license无效已过期");
                result.setResult(false);
                return result;
            }

            String mac = map.get("mac");
            String[] split = mac.split(",");
            boolean isMacContain=false;
            for (String macStr : split) {
            	if(selfMac != null && selfMac.contains(macStr)){
            		isMacContain=true;
            		break;
            	}
            }
            if(!isMacContain){
	            logger.info("licenseMac不匹配");
	            result.setErrMsg("licenseMac不匹配");
	            result.setResult(false);
	            object.put("selfMac", selfMac);
	            object.put("propertyMac", mac);
	            result.setExtend(object);
	            return result;
            }
        }
        logger.info("success!");
        result.setResult(true);
        return result;
    }


    public static Date parseDate(String DATE1) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return df.parse(DATE1);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }


    public static boolean compare_date(Date dt1, Date dt2) {
        return dt1.getTime() > dt2.getTime();
    }

    public static void main(String[] args) {
        new CheckLicense().check();
    }
}
