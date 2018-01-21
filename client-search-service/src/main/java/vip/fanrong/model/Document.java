package vip.fanrong.model;

import java.util.List;

/**
 * Created by Rong on 2018/1/19.
 */
public class Document {
    public static class TextField {
        String name;
        String value;
        Boolean store;

        public TextField() {

        }

        public TextField(String name, String value, Boolean store) {
            this.name = name;
            this.value = value;
            this.store = store;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Boolean getStore() {
            return store;
        }

        public void setStore(Boolean store) {
            this.store = store;
        }
    }

    public Document() {

    }

    private List<TextField> textFields;

    public List<TextField> getTextFields() {
        return textFields;
    }

    public void setTextFields(List<TextField> textFields) {
        this.textFields = textFields;
    }
}
