package cn.coolworks.handle;

import android.content.Intent;
import cn.coolworks.util.RequestDataEntity;

import com.hutuchong.util.BaseService;

public interface ActivityHandle {

	public void onBinddedService();

	public void onUpdateRequest(BaseService service,
			RequestDataEntity dataEntity);

	public void onStartIntent(Intent i);
}
