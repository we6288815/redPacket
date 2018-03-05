package com.lgguan.redpacket;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

public class MyRedPacketService extends AccessibilityService {

	private AccessibilityNodeInfo mRootNodeInfo = null;
	
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		
		
		int eventType = event.getEventType();
		
		switch (eventType) {
		case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
			getNotification(event);
			break;
			
		case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
			mRootNodeInfo = event.getSource();
			if (mRootNodeInfo == null) {
				return;
			}
			getPacket(event);
			break;
			
		case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
			mRootNodeInfo = event.getSource();
			if (mRootNodeInfo == null) {
				return;
			}
			openPacket(event);
			break;

		default:
			break;
		}
	}
	
	private void getNotification(AccessibilityEvent event){
		List<CharSequence> texts = event.getText();
		if (!texts.isEmpty()) {
			for(CharSequence text : texts){
				String content = text.toString();
				Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
				if (content.contains("[微信红包]")) {
					if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
						try {
							Toast.makeText(this, "有红包啦", Toast.LENGTH_SHORT).show();
							Notification notification = (Notification) event.getParcelableData();
							PendingIntent pendingIntent = notification.contentIntent;
							pendingIntent.send();
							break;
						} catch (CanceledException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	private void getPacket(AccessibilityEvent event){
		try {
			List<AccessibilityNodeInfo> hongbaoList = mRootNodeInfo.findAccessibilityNodeInfosByText("微信红包");
			if (hongbaoList != null && hongbaoList.size() > 0) {
				for (int i = hongbaoList.size() - 1; i >= 0; i --) 
				{
					AccessibilityNodeInfo curNodeInfo = hongbaoList.get(i).getParent();
					if (curNodeInfo != null && "android.widget.LinearLayout".equals(curNodeInfo.getClassName()) && "领取红包".equals(curNodeInfo.getChild(1).getText().toString())) {
						curNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
					}
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	private void openPacket(AccessibilityEvent event){
		try {
			/*boolean isHbpw = false;
			for (int i=0; i<mRootNodeInfo.getChildCount(); i++){
				List<AccessibilityNodeInfo> hbpw = mRootNodeInfo.getChild(i).findAccessibilityNodeInfosByText("手慢了，红包派完了");
				if (hbpw != null && hbpw.size() > 0) {
					isHbpw = true;
					break ;
				}
			}*/
			for (int i=0; i<mRootNodeInfo.getChildCount(); i++){
				List<AccessibilityNodeInfo> kksq = mRootNodeInfo.getChild(i).findAccessibilityNodeInfosByText("看看大家的手气");
				if (kksq != null && kksq.size() > 0) {
					AccessibilityNodeInfo kksqCurrInfo = kksq.get(kksq.size() - 1).getParent();
					kksqCurrInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
					return ;
				}
			}
			boolean hasHb = false;
			for (int i = 0; i < mRootNodeInfo.getChildCount(); i++) {
				List<AccessibilityNodeInfo> clickedWindowList = mRootNodeInfo.getChild(i).findAccessibilityNodeInfosByText("发了一个红包");
				if (clickedWindowList != null && clickedWindowList.size() > 0) {
					hasHb = true;
				}
			}
			if (hasHb) {
				for (int i=0; i<mRootNodeInfo.getChildCount(); i++){
					if ("android.widget.Button".equals(mRootNodeInfo.getChild(i).getClassName())) {
						mRootNodeInfo.getChild(i).performAction(AccessibilityNodeInfo.ACTION_CLICK);
						break;
					}
				}
			}
			
			if ("当前所在页面,红包详情".equals(mRootNodeInfo.getContentDescription())) {
				Thread.sleep(1000);
				performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onInterrupt() {
		Toast.makeText(getApplicationContext(), "红包来吧服务关闭", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onServiceConnected() {
		super.onServiceConnected();
		Toast.makeText(getApplicationContext(), "红包来吧服务开启", Toast.LENGTH_SHORT).show();
	}
}
