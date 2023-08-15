package lis.astmdevices;

import java.util.ArrayList;
import lis.AbstractDevice;
import lis.Result;

/**
 *
 * @author s862008
 */

public abstract class ASTMDevice extends AbstractDevice {

    protected String sender = "";
    protected String reciever = "HOST";
    protected String password = "";

    ArrayList<String> MessagesFromAnl = new ArrayList<>();
    protected ArrayList<StringBuilder> MessagesToAnl = new ArrayList<>();
    protected String currentLabID = "";
    protected String currentControl = "";

    public ASTMDevice(Communicator c, Connection connection, int idDevice) {
        super(c, connection, idDevice);
    }

    @Override
    public void Processing(String s) {

        //ENQ
        if (s.equals(AbstractDevice.CONTROL_CHARACTERS[5])) {
            mSendASK();
            MessagesFromAnl.clear();
            temp = new StringBuffer(0);
        }
        //ETX
        if (s.equals(AbstractDevice.CONTROL_CHARACTERS[3])) {
            MessagesFromAnl.add(temp.toString());
            temp = new StringBuffer(0);
        }
        //ETB
        if (s.equals(AbstractDevice.CONTROL_CHARACTERS[23])) {
            MessagesFromAnl.add(temp.toString());
            temp = new StringBuffer(0);
        }
        //STX
        if (s.equals(AbstractDevice.CONTROL_CHARACTERS[2])) {
            temp = new StringBuffer(0);
        }

        if (!s.equals(AbstractDevice.CONTROL_CHARACTERS[2])) {
            temp = temp.append(s);
        }

        // после LF отвечаем положительно ACK
        if (s.equals(AbstractDevice.CONTROL_CHARACTERS[10])) {
            mSendASK();
        }
        //CR
        if (s.equals(AbstractDevice.CONTROL_CHARACTERS[13])) {
          // nothing action
        }
        //EOT
        if (s.equals(AbstractDevice.CONTROL_CHARACTERS[4])) {
            parseRecord(MessagesFromAnl);
            MessagesFromAnl.clear();

        }
        //ACK
        if (s.equals(AbstractDevice.CONTROL_CHARACTERS[6])
                && !currentLabID.isEmpty()) { 
 
            if (!MessagesToAnl.isEmpty()) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException ex) {
                    System.out.println("ошибка задержки");
                }
                mSendMessage(MessagesToAnl.remove(0));
            } else {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException ex) {
                    System.out.println("ошибка задержки");
                }

                mSendEOT();

            }

        }

    }

    protected void parseRecord(ArrayList<String> ArrMessages) {
        String[] str;
        Result r = null;
        for (String s : ArrMessages) {

            str = s.split("\\|");

            if (str[0].contains("H")) {
                // Example: String version = str[1]; String name = str[3];
                continue;
            }

            if (str[0].contains("L")) {
                // Example: String recordCount = str[1]; String endFlag = str[2];
                continue;
            }

            if (str[0].contains("P")) {
                // Example: String patientId = str[2]; String patientName = str[4];
                continue;
            }

            if (str[0].contains("O")) {
                // если результат уже есть, а пришло Р сохраняем рез-т
                if (r != null) {
                    mSaveResults(r);
                }
                // запоминаем LabID
                r = new Result(str[2]);
                continue;
            }

            if ((str[0].contains("R")) && (r != null)) {
                String[] test = str[2].split("\\^");
                String[] result = str[3].split("\\^");
               
                // float value = Float.parseFloat(result[0]);
                r.putValue(test[3], result[0]);
                continue;
            }

            if (str[0].contains("Q")) {
                // send ENQ                
                currentLabID = str[2].replaceAll("\\D+", "");
                if (!currentLabID.isEmpty()) {

                    // если examid нормальный, то генерируем список сообщений
                    generateMessages(getCurrentLabID());

                    // отправляем ENQ
                    currentControl = AbstractDevice.CONTROL_CHARACTERS[5];//"ENQ"
                    byte[] arrb = new byte[1];
                    arrb[0] = 5;
                    Write(arrb);
                }
            }
        }

        // если есть результат
        if (r != null) {
            mSaveResults(r);
        }
    }

   

    protected void generateMessages(String labid) {
        MessagesToAnl.clear();
        String[] tests ;

        MessagesToAnl.add(new StringBuilder("1H|\\^&||" + password + "|" + reciever + "|||||" + sender + "||||"));

        MessagesToAnl.add(new StringBuilder("2P|1|" + examid));
        int i = 1;
        int frameid = 3;

        for (String test : tests) {
            StringBuilder st = new StringBuilder(String.valueOf(frameid) + "O|" + String.valueOf(i) + "|" + labid + "||" + test);
            MessagesToAnl.add(st);
            i++;
            if (frameid > 6) {
                frameid = 0;
            } else {
                frameid++;
            }
        }
        MessagesToAnl.add(new StringBuilder(String.valueOf(frameid) + "L|1|F"));

    }

    protected void mSendASK() {
        byte[] arrb = new byte[1];
        arrb[0] = 6;
        Write(arrb);
    }

    private void mSendEOT() {
        byte[] arrb = new byte[1];
        arrb[0] = 4;
        Write(arrb);
    }

    protected void mSendMessage(StringBuilder message) {
        Write(new byte[]{2});//<STX>
        Write(message.toString().getBytes());// message
        Write(new byte[]{13});//<CR>
        Write(new byte[]{3});//<ETX>
        Write(checksum(message.toString()).getBytes()); //CRC, Checksum
        Write(new byte[]{13});//<CR>
        Write(new byte[]{10});//<LF>

    }

    protected String checksum(String message) {
        byte[] data = message.getBytes();
        int intCRC = 0;
        try {
            for (int i = 0; i < data.length; i++) {
                intCRC += data[i];
            }
        } catch (Exception e) {
            System.out.println(intCRC);
            System.out.println(e);
        }

        intCRC += 16;
        String hexCrc = Integer.toHexString(intCRC);
        return hexCrc.substring(hexCrc.length() - 2).toUpperCase();

    }

   
    /**
     * @return the currentLabID
     */
    public String getCurrentLabID() {
        return currentLabID;
    }

}
