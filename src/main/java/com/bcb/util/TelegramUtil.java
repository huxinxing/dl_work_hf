package com.bcb.util;

import java.util.Date;

import com.bcb.bean.SystemProperties;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

public class TelegramUtil {
	private static Logger log = Logger.getLogger(TelegramUtil.class);
	
	private static String bot_username = "x_alert_msg_bot";

	// 钱包余额提醒
	//private static final Long sendAlertMsgChatId = -266931396L; // 正式
	//private static final Long testSendAlertMsgChatId = -205165964L; // 测试环境

	public static String sendAlertMsg( Long chatId,String msg) throws Exception {
		log.info("-----发送Telegram消息类型:-----sendAlertMsg");
		return sendTelegramMsg(chatId, bot_username, msg);
	}

	private static String sendTelegramMsg(Long chatId, String userName, String msg) throws Exception {
		Integer timestamp = Integer.valueOf(String.valueOf(new Date().getTime() / 1000));
		String chatIdStr = chatId.toString();
		String key = MD5Util.string2MD5(userName + "GF5ggR0P" + timestamp + chatIdStr).substring(0, 10);

		JSONObject obj = new JSONObject();
		obj.put("bot_username", userName);
		obj.put("bot_token", "120");
		obj.put("chat_id", chatIdStr);
		obj.put("text", msg);
		obj.put("timestamp", timestamp);
		obj.put("key", key);

		log.info("-----发送Telegram消息参数:-----" + obj.toJSONString());

		String url = "http://sendmessage.tbots.pro/";
		String result = StringEscapeUtils.unescapeJava(HttpPostUtil.httpPostWithJSON(url, obj.toJSONString()));

		log.info("-----发送Telegram消息结果:-----" + result);
		return result;
	}
}
