//Josh Seeley
import java.io.*;

import java.util.Scanner;

public class assignment2 {

  static boolean isCB = false;
  static boolean isD = false;
  static boolean isR = false;
  static boolean isI = false;
  static boolean isB = false;

    public static void main(String[] args) {
       
          if (args.length < 1) {
          System.out.println("Usage: java assignment2 <input-file> OR \n sh run.sh <input-file>");

          System.exit(1);
          }
          
          
         File f = new File(args[0]);

          
          try (InputStream inputStream = new FileInputStream(f)) {

            Scanner scnr = new Scanner(inputStream);
            while(scnr.hasNextLine()){
                String binaryString = scnr.nextLine();
      
        for (int i = 0; i < binaryString.length();  i += 32) {
            String binaryInstruction = binaryString.substring(i, i + 32);
            long instruction = Long.parseLong(binaryInstruction, 2);
            String assemblyCode = decodeInstruction(instruction);
            System.out.println(assemblyCode);
        
            }
            
        }
             } catch (IOException e) {
          e.printStackTrace();
          }  
         
    
}

//converts bytes to long
    private static long readNextInstruction(byte[] binaryData, int pc) {
        // Read 4 bytes (32 bits) from the binary data and convert to a long
        if (pc + 3 < binaryData.length) {

            return ((binaryData[pc] & 0xFFL) << 24) |
                    ((binaryData[pc + 1] & 0xFFL) << 16) |
                    ((binaryData[pc + 2] & 0xFFL) << 8) |
                    (binaryData[pc + 3] & 0xFFL);
        } else {
            System.err.println("Error: Not enough bytes to read");
            return 0;

        }
    }



    /*
     * 
     * Decoding functions 
     */
//Decodes R format
    private static String decodeRD(long instruction) {
        int rd = (int) (instruction & 0x1F); // Bits 4-0 for R-format
        int rn = (int) ((instruction >> 5) & 0x1F); // Bits 9-5
        int rm = (int) ((instruction >> 16) & 0x1F); // Bits 4-0
        int shamt = (int) ((instruction >> 10) & 0x3F); // Bits 15-10

        return String.format(" X%d, X%d, X%d", rd, rn, rm);
    }
//Decodes I format
private static String decodeI(long instruction) {
        int rd = (int) ((instruction >> 4) & 0x1F); // Bits 4-0 for R-format
        int rn = (int) ((instruction >> 5) & 0x1F); // Bits 9-5
        int ALU_immediate = (int) ((instruction >> 10) & 0xFFF); // Bits 21-10

        return String.format(" X%d, X%d, #%d", rd, rn, ALU_immediate);
    }
//Decodes CB format
    private static String decodeCB(long instruction) {
        int rt = (int) (instruction & 0x1F);
        int condBranchAddress = (int) ((instruction >> 5) & 0x7FFFF);

        return String.format(" X%d, %s", rt, decodeCondition(condBranchAddress));
    }

//Decodes B format
      private static String decodeB(long instruction) {

        int branchAddress = (int) ((instruction >> 0) & 0x3FFFFFF);

        return String.format(" %s", decodeCondition(branchAddress));
    }
//Decodes BR for R format
          private static String decodeRDnew(long instruction) {

            int BRinst = (int) ((instruction >> 5) & 0x1F);

        return String.format(" %s", decodeCondition(BRinst));
    }
//Decodes R shifts
    private static String decodeRDshift(long instruction) {
        int rd = (int) ((instruction >> 11) & 0x1F); // Bits 15-11 for R-format
        int rn = (int) ((instruction >> 5) & 0x1F); // Bits 9-5
        int shamt = (int) ((instruction >> 10) & 0x3F); // Bits 15-10

        return String.format(" X%d, X%d, #%d", rd, rn, shamt);
    }
//Decodes D format
    private static String decodeD(long instruction) {
        int rd = (int) ((instruction >> 11) & 0x1F); 
        int rn = (int) ((instruction >> 5) & 0x1F); // Bits 9-5
        int dtAddress = (int) ((instruction >> 12) & 0xFF); // Bits 20-12
        int op = (int) ((instruction >> 10) & 0x3); // Bits 11-10
        int rt = (int) (instruction & 0x1F); // Bits 4-0
    
        return String.format(" X%d, [X%d, #%d]", rt, rn, dtAddress);
    }
    
    //Decodes PRNT/PRNL/HALT/DUMP
    private static String decodeRDcustom(long instruction) {
     int rd = (int) (instruction & 0x1F);
    
        return String.format(" X%d", rd);
    }



    private static String decodeInstruction(long instruction) {
        //opcode lengths for different formats
        int RDopcode = (int) ((instruction >> 21) & 0x7FF); // Bits 31-21
        int Iopcode = (int) ((instruction >> 22) & 0x3FF); // Bits 31-22
        int CBopcode = (int) ((instruction >> 24) & 0xFF); // Bits 31-24
        int Bopcode = (int) ((instruction >> 26) & 0x3F); // Bits 31-26
        int Dopcode = (int) ((instruction >> 21) & 0x7FF); // Bits 31-21
        
        if((RDopcode == 0b10001011000) || (RDopcode == 0b10001010000) || (RDopcode == 0b11001010000) || (RDopcode == 0b10101010000) || (RDopcode == 0b11001011000) || (RDopcode == 0b11101011000) || (RDopcode == 0b10011011000) || (RDopcode == 0b11010110000) || (RDopcode == 0b11010011010) || (RDopcode == 0b11010011011) ||
        (RDopcode == 0b11111111101) || (RDopcode == 0b11111111100) || (RDopcode == 0b11111111110) || (RDopcode == 0b11111111111)){
            isR = true;
        switch (RDopcode){

         case 0b10001011000:
         return "ADD" + decodeRD(instruction);
            case 0b10001010000:
            return "AND" + decodeRD(instruction);
            case 0b10001010010: 
            return "EOR" + decodeRD(instruction);
            case 0b10001010001: 
            return "ORR" + decodeRD(instruction);
            case 0b10001011010:
            return "XOR" + decodeRD(instruction);
            case 0b11010011001:
            return "LSR" + decodeRD(instruction);
            case 0b10011011000:
            return "MUL" + decodeRD(instruction);
            case 0b11010110000:
            return "BR" + decodeRDnew(instruction);
            case 0b11010011010:
            return "LSR" + decodeRDshift(instruction);
            case 0b11010011011:
            return "LSL" + decodeRDshift(instruction);
            case 0b11111111101:
            return "PRNT" + decodeRDcustom(instruction);
            case 0b11111111100:
            return "PRNL";
            case 0b11111111110:
            return "DUMP";
            case 0b11111111111:
            return "HALT";

                default: 
                return "Error";
        }
    }
    if((Iopcode == 0b1001000100) || (Iopcode == 0b1001001000) || (Iopcode == 0b1101001000) || (Iopcode == 0b1011001000) || (Iopcode == 0b1101000100) || (Iopcode == 0b1111000100)){
         isI = true;
        switch(Iopcode){
            
            case 0b1001000100:
            return "ADDI" + decodeI(instruction);
            case 0b1001001000:
            return "ANDI" + decodeI(instruction);
            case 0b1101001000:
            return "EORI" + decodeI(instruction);
            case 0b1011001000:
            return "ORRI" + decodeI(instruction);
            case 0b1101000100:
            return "SUBI" + decodeI(instruction);
            case 0b1111000100:
            return "SUBIS" + decodeI(instruction);

            default:
            return "Error";
        }
    }
    if((CBopcode == 0b01010100) || (CBopcode == 0b10110101) || (CBopcode == 0b10110100)){
        isCB = true;
        switch(CBopcode){
            case 0b01010100:
            return "b.cond" + decodeCB(instruction);
            case 0b10110101:
            return "CBNZ" + decodeCB(instruction);
            case 0b10110100:
            return "CBZ" + decodeCB(instruction);

            default:
            return "Error";
        }
    }
     if((Bopcode == 0b100101) || (Bopcode == 0b000101)){
        isB = true;
        switch(Bopcode){
            case 0b100101:
            return "BL" + decodeB(instruction);
            case 0b000101:
            return "B" + decodeB(instruction);

            default:
            return "Error";
        }
    }
      if((Dopcode == 0b11111000000) || (Dopcode == 0b11111000010)){
        isD=true;
        switch(Dopcode){
            case 0b11111000000:
            return "STUR" + decodeD(instruction);
            case 0b11111000010:
            return "LDUR" + decodeD(instruction);

            default:
            return "Error";
        }
    }

    return "Error: Unknown opcode";
    }
//conditions to decode
    private static String decodeCondition(int cond) {
        switch (cond) {
            case 0x0:
                return "EQ";
            case 0x1:
                return "NE";
            case 0x2:
                return "HS";
            case 0x3:
                return "LO";
            case 0x4:
                return "MI";
            case 0x5:
                return "PL";
            case 0x6:
                return "VS";
            case 0x7:
                return "VC";
            case 0x8:
                return "HI";
            case 0x9:
                return "LS";
            case 0xA:
                return "GE";
            case 0xB:
                return "LT";
            case 0xC:
                return "GT";
            case 0xD:
                return "LE";
            default:
                return "Unknown";
        }
    }

}