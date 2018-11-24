package com.idx.naboo.user.personal_center.order;

import java.util.List;

/**
 * Created by ryan on 18-5-11.
 * Email: Ryan_chan01212@yeah.net
 */

public class Order_time {


    /**
     * ret : 200
     * data : {"action":"execute","content":{"display":"好的","display_guide":[],"error_code":0,"reply":{"order_list":[{"login_flag":true,"orderTypeFlag":-1,"orders":[]}]},"semantic":{"ShowWord":["打开"],"TIME":["2018-05-11:13:00:00,2018-05-11:18:59:59"]},"summary":{},"total_count":1,"tts":"好的","type":"order_list"},"domain":"order","intention":"instructing","query":"打开今天下午的订单","queryid":"d0b973df-4f5a-415d-988d-41f6887d9d158867658","terms":"打开 今天 下午 的 订单"}
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
         * content : {"display":"好的","display_guide":[],"error_code":0,"reply":{"order_list":[{"login_flag":true,"orderTypeFlag":-1,"orders":[]}]},"semantic":{"ShowWord":["打开"],"TIME":["2018-05-11:13:00:00,2018-05-11:18:59:59"]},"summary":{},"total_count":1,"tts":"好的","type":"order_list"}
         * domain : order
         * intention : instructing
         * query : 打开今天下午的订单
         * queryid : d0b973df-4f5a-415d-988d-41f6887d9d158867658
         * terms : 打开 今天 下午 的 订单
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
             * display : 好的
             * display_guide : []
             * error_code : 0
             * reply : {"order_list":[{"login_flag":true,"orderTypeFlag":-1,"orders":[]}]}
             * semantic : {"ShowWord":["打开"],"TIME":["2018-05-11:13:00:00,2018-05-11:18:59:59"]}
             * summary : {}
             * total_count : 1
             * tts : 好的
             * type : order_list
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
                private List<OrderListBean> order_list;

                public List<OrderListBean> getOrder_list() {
                    return order_list;
                }

                public void setOrder_list(List<OrderListBean> order_list) {
                    this.order_list = order_list;
                }

                public static class OrderListBean {
                    /**
                     * login_flag : true
                     * orderTypeFlag : -1
                     * orders : []
                     */

                    private boolean login_flag;
                    private int orderTypeFlag;
                    private List<?> orders;

                    public boolean isLogin_flag() {
                        return login_flag;
                    }

                    public void setLogin_flag(boolean login_flag) {
                        this.login_flag = login_flag;
                    }

                    public int getOrderTypeFlag() {
                        return orderTypeFlag;
                    }

                    public void setOrderTypeFlag(int orderTypeFlag) {
                        this.orderTypeFlag = orderTypeFlag;
                    }

                    public List<?> getOrders() {
                        return orders;
                    }

                    public void setOrders(List<?> orders) {
                        this.orders = orders;
                    }
                }
            }

            public static class SemanticBean {
                private List<String> ShowWord ;

                private List<String> TIME ;

                public List<String> getShowWord() {
                    return ShowWord;
                }

                public void setShowWord(List<String> showWord) {
                    ShowWord = showWord;
                }

                public List<String> getTIME() {
                    return TIME;
                }

                public void setTIME(List<String> TIME) {
                    this.TIME = TIME;
                }
            }

            public static class SummaryBean {
            }
        }
    }
}
