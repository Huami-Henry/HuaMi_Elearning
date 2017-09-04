package com.huami.elearning.model;

/**
 * 保存每个下载线程下载信息类
 */
public class DownloadInfo {

	private int threadId; // 下载线程的id
	private long startPos; // 开始点
	private long endPos; // 结束点
	private long compeleteSize; // 完成度
	private String url; // 下载文件的URL地址

	/**
	 * 
	 * @param threadId
	 *            下载线程的id
	 * @param startPos
	 *            开始点
	 * @param endPos
	 *            结束点
	 * @param compeleteSize
	 *            // 已下载的大小
	 * @param url
	 *            下载地址
	 */
	public DownloadInfo(int threadId, long startPos, long endPos,
						long compeleteSize, String url) {
		this.threadId = threadId;
		this.startPos = startPos;
		this.endPos = endPos;
		this.compeleteSize = compeleteSize;
		this.url = url;
	}

	public DownloadInfo() {
	}

	/** 获取下载地址 */
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getThreadId() {
		return threadId;
	}

	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}

	public long getStartPos() {
		return startPos;
	}

	public void setStartPos(long startPos) {
		this.startPos = startPos;
	}

	public long getEndPos() {
		return endPos;
	}

	public void setEndPos(long endPos) {
		this.endPos = endPos;
	}

	public long getCompeleteSize() {
		return compeleteSize;
	}

	public void setCompeleteSize(long compeleteSize) {
		this.compeleteSize = compeleteSize;
	}

	@Override
	public String toString() {
		return "DownloadInfo [threadId=" + threadId + ", startPos=" + startPos
				+ ", endPos=" + endPos + ", compeleteSize=" + compeleteSize
				+ "]";
	}
}
