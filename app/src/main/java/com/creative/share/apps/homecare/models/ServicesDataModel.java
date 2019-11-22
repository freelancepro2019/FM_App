package com.creative.share.apps.homecare.models;


import java.io.Serializable;
import java.util.List;

public class ServicesDataModel implements Serializable {
    private List<ServiceModel> services;

    public class ServiceModel implements Serializable {
        private int service_id;
        private String logo;
        private WordsModel words;

        public WordsModel getWords() {
            return words;
        }

        public int getService_id() {
            return service_id;
        }

        public String getLogo() {
            return logo;
        }

        public class WordsModel implements Serializable {
            private String title;

            public String getTitle() {
                return title;
            }
        }
    }

    public List<ServiceModel> getServices() {
        return services;
    }

}
