package com.huami.elearning.model;

/**
 * Created by Administrator on 2017/5/10.
 */

public class APPVersionInfo {
    private VersionBean version;

    public VersionBean getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "APPVersionInfo{" +
                "version=" + version +
                '}';
    }

    public void setVersion(VersionBean version) {
        this.version = version;
    }
    public static class VersionBean {
        private String latestversion;
        private String url;
        private int versioncode;

        public String getLatestversion() {
            return latestversion;
        }

        public void setLatestversion(String latestversion) {
            this.latestversion = latestversion;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getVersioncode() {
            return versioncode;
        }

        public void setVersioncode(int versioncode) {
            this.versioncode = versioncode;
        }

        @Override
        public String toString() {
            return "VersionBean{" +
                    "latestversion='" + latestversion + '\'' +
                    ", url='" + url + '\'' +
                    ", versioncode=" + versioncode +
                    '}';
        }
    }
}
