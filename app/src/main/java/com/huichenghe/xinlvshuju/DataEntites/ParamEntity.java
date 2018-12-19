package com.huichenghe.xinlvshuju.DataEntites;

/**
 * 检查更新返回的版本信息实体
 * Created by DIY on 2015/12/22.
 */
public class ParamEntity {

        private String version;

        private String minVersion;

        private String description;

        private String url;

        private int fileSize;

        public void setVersion(String version){
            this.version = version;
        }
        public String getVersion(){
            return this.version;
        }
        public void setMinVersion(String minVersion){
            this.minVersion = minVersion;
        }
        public String getMinVersion(){
            return this.minVersion;
        }
        public void setDescription(String description){
            this.description = description;
        }
        public String getDescription(){
            return this.description;
        }
        public void setUrl(String url){
            this.url = url;
        }
        public String getUrl(){
            return this.url;
        }
        public void setFileSize(int fileSize){
            this.fileSize = fileSize;
        }
        public int getFileSize(){
            return this.fileSize;
        }

    }

