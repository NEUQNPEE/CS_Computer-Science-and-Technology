`timescale 1ns / 1ns
`include "define.vh"
module if_id(
    input wire clk,
    input wire rst,
    // 取指阶段获得的指令
    input[`InstBus] if_inst,
    // 译码阶段的指令
    output reg[`InstBus] id_inst,
    
    // 当前地址
    input wire[`InstAddrBus] if_pc,
    output reg[`InstAddrBus] id_pc,
    
    // 流水线暂停
    input wire[5:0] stall
    
    );
    
    always @ (posedge clk) begin
        if (rst == `RstEnable) begin
            id_pc <= `ZeroWord;
            id_inst <= `ZeroWord;
            // 意为取指暂停，译码继续，那么传送一个空指令
        end else if(stall[1] == `Stop && stall[2] == `NoStop) begin
            id_pc <= `ZeroWord;
            id_inst <= `ZeroWord;
            // 正常流动    
      end else if(stall[1] == `NoStop) begin
          id_pc <= if_pc;
          id_inst <= if_inst;
        end
    end
    
endmodule
