`timescale 1ns / 1ns
`include "define.vh"
module pc_reg(

    input wire rst, // 复位
    input wire clk, 
    output reg[31:0] pc, //地址
    output reg ce, // 使能
    
    input wire branch_flag_i,  // 是否发生转移
    input wire[`RegBus] branch_target_address_i,  //转移地址
    // ctrl发出信号决定流水线是否暂停（即pc是否继续自增）
    input wire[5:0] stall
    );
    // 复位信号有效则芯片禁止，否则使能
    always@(posedge clk) begin
        if(rst==`RstEnable) begin
            ce<=`ChipDisable;
        end else begin
            ce<=`ChipEnable;
        end
    end
    
    // 芯片禁止时置计数器置0，否则自增（使能）
    always@(posedge clk) begin
        if(ce==`ChipDisable) begin
            pc<=32'h0000_0000;
            // 如果发生转移就跳转到指定地址
        end else if(branch_flag_i == `Branch) begin
            pc<=branch_target_address_i;
         //如果流水线没让暂停就继续
        end else if(stall[0]==`NoStop) begin
            // 一条指令32位 -> 4字节
            pc <= pc + 4'h4;
        end
    end
endmodule
