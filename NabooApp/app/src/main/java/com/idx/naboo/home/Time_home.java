package com.idx.naboo.home;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ryan on 18-4-20.
 * Email: Ryan_chan01212@yeah.net
 */

public class Time_home {

    /**
     * ret : 200
     * data : {"action":"execute","content":{"display":"今天是谷雨","display_guide":[],"error_code":0,"reply":{"default":[{"str":"谷雨"}]},"semantic":{"TIME":["2018-04-20,2018-04-20"]},"summary":{},"total_count":0,"tts":"今天是谷雨","type":"default"},"domain":"time","intention":"searching","query":"今天是什么节日","queryid":"d0b973df-4f5a-415d-988d-41f6887d9d158867658","terms":"今天 是 什么 节日"}
     * msg :
     */

    private int ret;
    private DataBean data;
    private String msg;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBean {
        /**
         * action : execute
         * content : {"display":"今天是谷雨","display_guide":[],"error_code":0,"reply":{"default":[{"str":"谷雨"}]},"semantic":{"TIME":["2018-04-20,2018-04-20"]},"summary":{},"total_count":0,"tts":"今天是谷雨","type":"default"}
         * domain : time
         * intention : searching
         * query : 今天是什么节日
         * queryid : d0b973df-4f5a-415d-988d-41f6887d9d158867658
         * terms : 今天 是 什么 节日
         */

        private String action;
        private ContentBean content;
        private String domain;
        private String intention;
        private String query;
        private String queryid;
        private String terms;

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public ContentBean getContent() {
            return content;
        }

        public void setContent(ContentBean content) {
            this.content = content;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public String getIntention() {
            return intention;
        }

        public void setIntention(String intention) {
            this.intention = intention;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public String getQueryid() {
            return queryid;
        }

        public void setQueryid(String queryid) {
            this.queryid = queryid;
        }

        public String getTerms() {
            return terms;
        }

        public void setTerms(String terms) {
            this.terms = terms;
        }

        public static class ContentBean {
            /**
             * display : 今天是谷雨
             * display_guide : []
             * error_code : 0
             * reply : {"default":[{"str":"谷雨"}]}
             * semantic : {"TIME":["2018-04-20,2018-04-20"]}
             * summary : {}
             * total_count : 0
             * tts : 今天是谷雨
             * type : default
             */

            private String display;
            private int error_code;
            private ReplyBean reply;
            private SemanticBean semantic;
            private SummaryBean summary;
            private int total_count;
            private String tts;
            private String type;
            private List<?> display_guide;

            public String getDisplay() {
                return display;
            }

            public void setDisplay(String display) {
                this.display = display;
            }

            public int getError_code() {
                return error_code;
            }

            public void setError_code(int error_code) {
                this.error_code = error_code;
            }

            public ReplyBean getReply() {
                return reply;
            }

            public void setReply(ReplyBean reply) {
                this.reply = reply;
            }

            public SemanticBean getSemantic() {
                return semantic;
            }

            public void setSemantic(SemanticBean semantic) {
                this.semantic = semantic;
            }

            public SummaryBean getSummary() {
                return summary;
            }

            public void setSummary(SummaryBean summary) {
                this.summary = summary;
            }

            public int getTotal_count() {
                return total_count;
            }

            public void setTotal_count(int total_count) {
                this.total_count = total_count;
            }

            public String getTts() {
                return tts;
            }

            public void setTts(String tts) {
                this.tts = tts;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public List<?> getDisplay_guide() {
                return display_guide;
            }

            public void setDisplay_guide(List<?> display_guide) {
                this.display_guide = display_guide;
            }

            public static class ReplyBean {
                @SerializedName("default")
                private List<DefaultBean> defaultX;

                public List<DefaultBean> getDefaultX() {
                    return defaultX;
                }

                public void setDefaultX(List<DefaultBean> defaultX) {
                    this.defaultX = defaultX;
                }

                public static class DefaultBean {
                    /**
                     * str : 谷雨
                     */

                    private String str;

                    public String getStr() {
                        return str;
                    }

                    public void setStr(String str) {
                        this.str = str;
                    }
                }
            }

            public static class SemanticBean {
            }

            public static class SummaryBean {
            }
        }
    }
}
