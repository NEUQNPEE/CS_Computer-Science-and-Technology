`timescale 1ns / 1ns
`include "define.vh"
module mem_wb(
	input	wire clk,
	input wire	rst,
	//来自访存阶段的信息	
	input wire[`RegAddrBus] mem_wd,
	input wire mem_wreg,
	input wire[`RegBus] mem_wdata,
	//送到回写阶段的信息
	output reg[`RegAddrBus] wb_wd,
	output reg wb_wreg,
	output reg[`RegBus] wb_wdata,	  
	// 流水暂停否
	input wire[5:0] stall
	
	// //移动指令 数据相关
    // input wire[`RegBus] mem_hi,
    // input wire[`RegBus] mem_lo,
    // input wire mem_whilo,   
    // output reg[`RegBus] wb_hi,
    // output reg[`RegBus] wb_lo,
    // output reg wb_whilo          
	
);
	always @ (posedge clk) begin
		if(rst == `RstEnable) begin
			wb_wd <= `NOPRegAddr;
			wb_wreg <= `WriteDisable;
		    wb_wdata <= `ZeroWord;	
		    
		    // wb_hi <= `ZeroWord;
            // wb_lo <= `ZeroWord;
            // wb_whilo <= `WriteDisable;
        end else if(stall[4] == `Stop && stall[5] == `NoStop) begin
             wb_wd <= `NOPRegAddr;
             wb_wreg <= `WriteDisable;
             wb_wdata <= `ZeroWord;    
	   end else if(stall[4] == `NoStop) begin
			wb_wd <= mem_wd;
			wb_wreg <= mem_wreg;
			wb_wdata <= mem_wdata;
			
            // wb_hi <= mem_hi;
            // wb_lo <= mem_lo;
            // wb_whilo <= mem_whilo;    
		end    //if
	end      //always
endmodule