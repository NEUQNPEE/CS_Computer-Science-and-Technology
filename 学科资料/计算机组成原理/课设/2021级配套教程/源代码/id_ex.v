`timescale 1ns / 1ns
`include "define.vh"
module id_ex(

	input	wire clk,
	input wire	rst,

	
	//从译码阶段传递的信息
	input wire[`AluOpBus] id_aluop,
	input wire[`RegBus] id_reg1,
	input wire[`RegBus] id_reg2,
	input wire[`RegAddrBus] id_wd,
	input wire id_wreg,	
	
	//传递到执行阶段的信息
	output reg[`AluOpBus] ex_aluop,
	output reg[`RegBus] ex_reg1,
	output reg[`RegBus] ex_reg2,
	output reg[`RegAddrBus] ex_wd,
	output reg ex_wreg,
	
	// 当前指令
	input wire[`RegBus] id_inst,
    output reg[`RegBus] ex_inst,
    
    // 分支跳转相关
    input wire[`RegBus] id_link_address,
    input wire id_is_in_delayslot,
    input wire next_inst_in_delayslot_i,
    
    output reg[`RegBus] ex_link_address,
    output reg ex_is_in_delayslot,
    output reg is_in_delayslot_o,
    
    //流水线暂停
    input wire[5:0] stall
);

    

	always @ (posedge clk) begin
		if (rst == `RstEnable) begin
			ex_aluop <= `NOP_OP;
			ex_reg1 <= `ZeroWord;
			ex_reg2 <= `ZeroWord;
			ex_wd <= `NOPRegAddr;
			ex_wreg <= `WriteDisable;
			
			ex_inst <= `ZeroWord;
			
			ex_link_address <= `ZeroWord;
            ex_is_in_delayslot <= `NotInDelaySlot;
            is_in_delayslot_o <= `NotInDelaySlot;
            // 译码暂停，执行继续，那么传送一个空指令
        end else if(stall[2] == `Stop && stall[3] == `NoStop) begin
            ex_aluop <= `NOP_OP;
            ex_reg1 <= `ZeroWord;
            ex_reg2 <= `ZeroWord;
            ex_wd <= `NOPRegAddr;
            ex_wreg <= `WriteDisable;
            
            ex_inst <= `ZeroWord;
            
            ex_link_address <= `ZeroWord;
            ex_is_in_delayslot <= `NotInDelaySlot;
            // 正常流动
		end else if(stall[2] == `NoStop) begin		
			ex_aluop <= id_aluop;
			ex_reg1 <= id_reg1;
			ex_reg2 <= id_reg2;
			ex_wd <= id_wd;
			ex_wreg <= id_wreg;	
			
			ex_inst <= id_inst;	
			
			ex_link_address <= id_link_address;
            ex_is_in_delayslot <= id_is_in_delayslot;
            is_in_delayslot_o <= next_inst_in_delayslot_i;
		end
	end
	
	//end else if(stall[2] ==、Stop && stall[3] ==、NoStop) begin
    // ex_ inst <=、ZeroWord;
    //end else if(stall[2] ==、NoStop) begin

	
endmodule