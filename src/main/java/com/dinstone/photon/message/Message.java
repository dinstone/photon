package com.dinstone.photon.message;

public interface Message {

    int getId();

    Type getType();

    byte getVersion();

    Headers getHeaders();

    Object getContent();

    public enum Type {
        HEARTBEAT((byte) 0), // MEP: long connection parttern
        REQUEST((byte) 1), // MEP: the request of the request-respose parttern
        RESPONSE((byte) 2), // MEP: the response of the request-respose parttern
        NOTICE((byte) 3); // MEP: one-way or notify parttern

        private byte value;

        private Type(byte value) {
            this.value = value;
        }

        /**
         * the value to get
         *
         * @return the value
         * @see Type#value
         */
        public byte getValue() {
            return value;
        }

        public static Type valueOf(byte value) {
            switch (value) {
            case 0:
                return HEARTBEAT;
            case 1:
                return REQUEST;
            case 2:
                return RESPONSE;
            case 3:
                return NOTICE;

            default:
                break;
            }
            throw new IllegalArgumentException("unsupported message type [" + value + "]");
        }

    }

}
