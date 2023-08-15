package lis;

public abstract class AbstractDevice {
  
public static final String[] CONTROL_CHARACTERS = {
        "<NULL>", //0
        "<SOH>", //1
        "<STX>", //2
        "<ETX>", //3
        "<EOT>", //4
        "<ENQ>", //5
        "<ACK>", //6
        "<BELL>", //7
        "<BS>", //8
        "<HT>", //9
        "<LF>", //10 0A
        "<LT>", //11 0B
        "<FF>", //12 0C
        "<CR>", //13 0D
        "<SO>", //14 0E
        "<SI>", //15 0F
        "<DLE>", //16 10
        "<DC1>", //17 11
        "<DC2>", //18 12
        "<DC3>", //19 13
        "<DC4>", //20 14
        "<NAK>", //21 15
        "<SI>", //22 16
        "<ETB>", //23 17
        "<CAN>", //24 18
        "<EOM>", //25 19
        "<SUB>", //26 1A
        "<ESC>"}; //27 1B

    public abstract void Processing(String s);

    public AbstractDevice() {

    }
  
    protected void mSaveResult(String examid, String analogy, String result, String flag) {
    //TO DO something
    }
    
    protected boolean Write(byte[] arrb) {
      // заглушка - здесь реализация отправки сообшения прибору 
        boolean isOK = true; 
        return isOK;
    }
  
}
