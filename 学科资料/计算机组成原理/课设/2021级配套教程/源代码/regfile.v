`timescale 1ns / 1ns
`include "define.vh"
module regfile(

    input wire clk,
    input wire rst,
    
    // write 
    input wire we,
    // 32个寄存器 -> 5位地址线
    input wire[`RegAddrBus] waddr,
    // 32位数据线
    input wire[`RegBus] wdata,
    
    //read 1
    input wire re1,
    input wire[`RegAddrBus] raddr1,
    output reg[`RegBus] rdata1,
    
    //read 2
    input wire re2,
    input wire[`RegAddrBus] raddr2,
    output reg[`RegBus] rdata2
    );
    
    // 32个 32位寄存器
    reg[`RegBus] regs[0:`RegNum-1];
    
    //initial begin
    //    regs[1]=32'h3876;
    //    regs[2]=32'h81123333;
    //end
            
    always @ (posedge clk) begin
        if(rst == `RstDisable) begin
            if((we == `WriteEnable) && (waddr != `RegNumLog2'h0)) begin
                regs[waddr] <= wdata;
            end
        end
    end
    
    always @(*) begin
        if(rst == `RstEnable) begin
            rdata1 <= `ZeroWord;
            // 复位信号无效 且 读0地址时 （强制读0地址为0）
        end else if (raddr1 == `RegNumLog2'h0) begin
            rdata1 <= `ZeroWord;
           // 如果第一个读寄存器端口要读取的目标寄存器与要写入的目的寄存器是同一个寄存
           // 器，那么直接将要写入的值作为第一个读寄存器端口的输出;
        end else if((raddr1 == waddr) && (we == `WriteEnable)
                    && (re1 == `ReadEnable)) begin
            rdata1 <= wdata;
            // 读寄存器的值
        end else if (re1 == `ReadEnable) begin
            rdata1 <= regs[raddr1];
        end else begin
            rdata1 <= `ZeroWord;
        end
      end
            
            
      always @(*) begin
        if(rst == `RstEnable) begin
            rdata2 <= `ZeroWord;
        end else if (raddr2 == `RegNumLog2'h0) begin
            rdata2 <= `ZeroWord;
        end else if((raddr2 == waddr) && (we == `WriteEnable)
                    && (re2 == `ReadEnable)) begin
            rdata2 <= wdata;
        end else if (re2 == `ReadEnable) begin
            rdata2 <= regs[raddr2];
        end else begin
            rdata2 <= `ZeroWord;
        end
    end
          
endmodule
