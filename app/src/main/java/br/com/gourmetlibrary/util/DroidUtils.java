package br.com.gourmetlibrary.util;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DroidUtils {

	public static Date convertStringToDate(String date){
		Date result = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try {
			result = format.parse(date);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static String convertDateToString(Date date){
		String result = "";
		
		//SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try {
			result = format.format(date);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
        public static String horaParaString(java.sql.Timestamp data) {
            
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            return sdf.format(data);
        } catch (Exception e) {
            Log.e("afaLog", "Houve um problema ao obter uma data (data para string)");
            return null;
        }
    }
         public static Date convertStringToDateBr(String date) {
        Date result = null;
        if (date.length() == 10) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            try {
                result = format.parse(date);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } if(date.length() == 23) {

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

            try {
                //Log("afaLog","DATA DO BANCO"+date);
                result = format.parse(date);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else{
         SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            try {
                //Log("afaLog","DATA DO BANCO"+date);
                result = format.parse(date);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return result;
    }

    public static Date convertStringToDateSimple(String date) {
        //Log("afaLog", "DATA SAIDA" + date);
        Date result = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String data = date.replace(" 00:00:00", "");
        try {
            result = format.parse(date);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;
    }
}
