package bgu.spl.net.impl.tftp.packets;

public enum OpCode {
    UNDEFINED,
    RRQ,
    WRQ,
    DATA,
    ACK,
    ERROR,
    DIRQ,
    LOGRQ,
    DELRQ,
    BCAST,
    DISC
}
