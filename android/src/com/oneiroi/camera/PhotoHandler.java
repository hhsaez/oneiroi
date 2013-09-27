package com.oneiroi.camera;

import java.io.File;
import java.io.FileOutputStream;

import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Environment;
import android.util.Log;

public class PhotoHandler implements PictureCallback {
	
	private String fileName;
	
	public PhotoHandler() {
		File pictureFileDir = getDir();
		if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
			Log.d("LLEGO", "Cannot create directory to save image: " + pictureFileDir.toString());
			return;
		}
		
		fileName = pictureFileDir.getPath() + File.separator + "oneiroi.jpg";
	}
	
	public String getFileName() {
		return fileName;
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		File pictureFile = new File(fileName);
		try {
			FileOutputStream fos = new FileOutputStream(pictureFile);
			fos.write(data);
			fos.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private File getDir() {
		File sdDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		return new File(sdDir, "Oneiroi");
	}

}
