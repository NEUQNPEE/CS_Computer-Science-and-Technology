`timescale 1ns / 1ns
`include "define.vh"
module inst_rom(
    input wire clk,
    input wire ce,
    input wire[`InstAddrBus] addr, //32位 ROM 地址总线
    output reg[`InstBus] inst //32位 ROM 数据总线
    );
    
    reg[`InstBus] inst_mem[0:`InstMemNum-1];  // [0:131071]个32位存储单元 -> 103072*32/8/1024/4 -> ROM实际大小 128KB
    
    initial $readmemh ("D:/FPGA/myFirstCpu/t1.data ",inst_mem);
    
    always @(*) begin
        // 芯片禁止则置 0
        if(ce==`ChipDisable) begin
            inst <= `ZeroWord;
        end else begin
            // 读取指令 inst_mem[addr[[18:2]]
            inst <= inst_mem[addr[`InstMemNumLog2 + 1:2]];
        end
    end
endmodule
