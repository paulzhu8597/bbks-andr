package com.funger.bbks.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Properties;
import java.util.UUID;

import com.funger.bbks.api.ApiClient;
import com.funger.bbks.bean.BookJson;
import com.funger.bbks.bean.DynamicJson;
import com.funger.bbks.bean.FriendJson;
import com.funger.bbks.bean.MessageJson;
import com.funger.bbks.bean.User;
import com.funger.bbks.bean.UserJson;
import com.funger.bbks.common.ImageUtils;
import com.funger.bbks.common.StringUtils;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

public class AppContext extends Application {
    public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_CMWAP = 0x02;
    public static final int NETTYPE_CMNET = 0x03;

    public static final int PAGE_SIZE = 20;// 默认分页大小
    private static final int CACHE_TIME = 60 * 60000;// 缓存失效时间

    private boolean login = false; // 登录状态
    private Long loginUid = -1L; // 登录用户的id
    private Hashtable<String, Object> memCacheRegion = new Hashtable<String, Object>();
    private User session;

    private String saveImagePath;// 保存图片路径

    @Override
    public void onCreate() {
	super.onCreate();
	// 注册App异常崩溃处理器
	Thread.setDefaultUncaughtExceptionHandler(AppException
		.getAppExceptionHandler());
	init();
    }

    /**
     * 初始化
     */
    private void init() {
	// 设置保存图片的路径
	saveImagePath = getProperty(AppConfig.SAVE_IMAGE_PATH);
	if (StringUtils.isEmpty(saveImagePath)) {
	    setProperty(AppConfig.SAVE_IMAGE_PATH,
		    AppConfig.DEFAULT_SAVE_IMAGE_PATH);
	    saveImagePath = AppConfig.DEFAULT_SAVE_IMAGE_PATH;
	}
    }

    public void loginSuccess(User user) {
	// set login
	this.setLogin(true);
	// set user info
	this.setSession(user);
	// set uid
	this.setLoginUid(user.getId());
    }

    /**
     * 用户登录
     * 
     * @param account
     * @param pwd
     * @return
     * @throws AppException
     */
    public UserJson login(String userName, String pwd) throws AppException {
	return ApiClient.login(this, userName, pwd);
    }

    public BookJson bookFind(int catlog,int pageNo,int pageSize) throws AppException {
	return ApiClient.getBooks(this, catlog,pageNo,pageSize);
    }
    
    public BookJson EBookFind(int catlog,int pageNo,int pageSize) throws AppException {
	return ApiClient.getEBooks(this, catlog,pageNo,pageSize);
    }

    public BookJson bookSearch(int pageNo, String keyWord) throws AppException {
	return ApiClient.searchBooks(this, keyWord, pageNo);
    }

    public FriendJson getFriends(int pageNo, int type) throws AppException {
	return ApiClient.getFriends(this, type, pageNo);
    }

    public MessageJson getMessages(int pageNo, int type) throws AppException {
	return ApiClient.getMessages(this, type, pageNo);
    }

    public DynamicJson getDynamics(int pageNo, int type) throws AppException {
	return ApiClient.getDynamics(this, type, pageNo);
    }

    public User getSession() {
	return session;
    }

    public void setSession(User session) {
	this.session = session;
    }

    public Long getLoginUid() {
	return loginUid;
    }

    public void setLoginUid(Long loginUid) {
	this.loginUid = loginUid;
    }

    /**
     * 保存用户头像
     * 
     * @param fileName
     * @param bitmap
     */
    public void saveUserFace(String fileName, Bitmap bitmap) {
	try {
	    ImageUtils.saveImage(this, fileName, bitmap);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public boolean isLogin() {
	return login;
    }

    public void setLogin(boolean login) {
	this.login = login;
    }

    /**
     * 获取用户头像
     * 
     * @param key
     * @return
     * @throws AppException
     */
    public Bitmap getUserFace(String key) throws AppException {
	FileInputStream fis = null;
	try {
	    fis = openFileInput(key);
	    return BitmapFactory.decodeStream(fis);
	} catch (Exception e) {
	    throw AppException.run(e);
	} finally {
	    try {
		fis.close();
	    } catch (Exception e) {
	    }
	}
    }

    /**
     * 获取App安装包信息
     * 
     * @return
     */
    public PackageInfo getPackageInfo() {
	PackageInfo info = null;
	try {
	    info = getPackageManager().getPackageInfo(getPackageName(), 0);
	} catch (NameNotFoundException e) {
	    e.printStackTrace(System.err);
	}
	if (info == null)
	    info = new PackageInfo();
	return info;
    }

    /**
     * 获取App唯一标识
     * 
     * @return
     */
    public String getAppId() {
	String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
	if (StringUtils.isEmpty(uniqueID)) {
	    uniqueID = UUID.randomUUID().toString();
	    setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
	}
	return uniqueID;
    }

    // /////////////////////////////////
    // /////////////////////////////////
    // /////////////////////////////////

    /**
     * 判断缓存数据是否可读
     * 
     * @param cachefile
     * @return
     */
    private boolean isReadDataCache(String cachefile) {
	return readObject(cachefile) != null;
    }

    /**
     * 判断缓存是否存在
     * 
     * @param cachefile
     * @return
     */
    private boolean isExistDataCache(String cachefile) {
	boolean exist = false;
	File data = getFileStreamPath(cachefile);
	if (data.exists())
	    exist = true;
	return exist;
    }

    /**
     * 判断缓存是否失效
     * 
     * @param cachefile
     * @return
     */
    public boolean isCacheDataFailure(String cachefile) {
	boolean failure = false;
	File data = getFileStreamPath(cachefile);
	if (data.exists()
		&& (System.currentTimeMillis() - data.lastModified()) > CACHE_TIME)
	    failure = true;
	else if (!data.exists())
	    failure = true;
	return failure;
    }

    /**
     * 清除缓存目录
     * 
     * @param dir
     *            目录
     * @param numDays
     *            当前系统时间
     * @return
     */
    private int clearCacheFolder(File dir, long curTime) {
	int deletedFiles = 0;
	if (dir != null && dir.isDirectory()) {
	    try {
		for (File child : dir.listFiles()) {
		    if (child.isDirectory()) {
			deletedFiles += clearCacheFolder(child, curTime);
		    }
		    if (child.lastModified() < curTime) {
			if (child.delete()) {
			    deletedFiles++;
			}
		    }
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	return deletedFiles;
    }

    /**
     * 将对象保存到内存缓存中
     * 
     * @param key
     * @param value
     */
    public void setMemCache(String key, Object value) {
	memCacheRegion.put(key, value);
    }

    /**
     * 从内存缓存中获取对象
     * 
     * @param key
     * @return
     */
    public Object getMemCache(String key) {
	return memCacheRegion.get(key);
    }

    /**
     * 保存磁盘缓存
     * 
     * @param key
     * @param value
     * @throws IOException
     */
    public void setDiskCache(String key, String value) throws IOException {
	FileOutputStream fos = null;
	try {
	    fos = openFileOutput("cache_" + key + ".data", Context.MODE_PRIVATE);
	    fos.write(value.getBytes());
	    fos.flush();
	} finally {
	    try {
		fos.close();
	    } catch (Exception e) {
	    }
	}
    }

    /**
     * 获取磁盘缓存数据
     * 
     * @param key
     * @return
     * @throws IOException
     */
    public String getDiskCache(String key) throws IOException {
	FileInputStream fis = null;
	try {
	    fis = openFileInput("cache_" + key + ".data");
	    byte[] datas = new byte[fis.available()];
	    fis.read(datas);
	    return new String(datas);
	} finally {
	    try {
		fis.close();
	    } catch (Exception e) {
	    }
	}
    }

    /**
     * 保存对象
     * 
     * @param ser
     * @param file
     * @throws IOException
     */
    public boolean saveObject(Serializable ser, String file) {
	FileOutputStream fos = null;
	ObjectOutputStream oos = null;
	try {
	    fos = openFileOutput(file, MODE_PRIVATE);
	    oos = new ObjectOutputStream(fos);
	    oos.writeObject(ser);
	    oos.flush();
	    return true;
	} catch (Exception e) {
	    e.printStackTrace();
	    return false;
	} finally {
	    try {
		oos.close();
	    } catch (Exception e) {
	    }
	    try {
		fos.close();
	    } catch (Exception e) {
	    }
	}
    }

    /**
     * 读取对象
     * 
     * @param file
     * @return
     * @throws IOException
     */
    public Serializable readObject(String file) {
	if (!isExistDataCache(file))
	    return null;
	FileInputStream fis = null;
	ObjectInputStream ois = null;
	try {
	    fis = openFileInput(file);
	    ois = new ObjectInputStream(fis);
	    return (Serializable) ois.readObject();
	} catch (FileNotFoundException e) {
	} catch (Exception e) {
	    e.printStackTrace();
	    // 反序列化失败 - 删除缓存文件
	    if (e instanceof InvalidClassException) {
		File data = getFileStreamPath(file);
		data.delete();
	    }
	} finally {
	    try {
		ois.close();
	    } catch (Exception e) {
	    }
	    try {
		fis.close();
	    } catch (Exception e) {
	    }
	}
	return null;
    }

    public boolean containsProperty(String key) {
	Properties props = getProperties();
	return props.containsKey(key);
    }

    public void setProperties(Properties ps) {
	AppConfig.getAppConfig(this).set(ps);
    }

    public Properties getProperties() {
	return AppConfig.getAppConfig(this).get();
    }

    public void setProperty(String key, String value) {
	AppConfig.getAppConfig(this).set(key, value);
    }

    public String getProperty(String key) {
	return AppConfig.getAppConfig(this).get(key);
    }

    public void removeProperty(String... key) {
	AppConfig.getAppConfig(this).remove(key);
    }

    /**
     * 获取内存中保存图片的路径
     * 
     * @return
     */
    public String getSaveImagePath() {
	return saveImagePath;
    }

    /**
     * 设置内存中保存图片的路径
     * 
     * @return
     */
    public void setSaveImagePath(String saveImagePath) {
	this.saveImagePath = saveImagePath;
    }

}