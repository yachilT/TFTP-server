package bgu.spl.net.impl.tftp;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.packets.BasePacket;

public class TftpMessagingProtocol implements MessagingProtocol<BasePacket>{

    @Override
    public BasePacket process(BasePacket msg) {
        
    }

    @Override
    public boolean shouldTerminate() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'shouldTerminate'");
    }
    
}
