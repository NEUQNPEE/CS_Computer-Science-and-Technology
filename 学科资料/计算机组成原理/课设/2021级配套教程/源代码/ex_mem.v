`timescale 1ns / 1ns
`include "define.vh"
module ex_mem(

	input	wire clk,
	input wire	rst,
	//来自执行阶段的信息	
	input wire[`RegAddrBus]  ex_wd,
	input wire ex_wreg,
	input wire[`RegBus] ex_wdata, 	
	//送到访存阶段的信息
	output reg[`RegAddrBus] mem_wd,
	output reg mem_wreg,
	output reg[`RegBus] mem_wdata,
	
	// load/store相关输入口
	input wire[`AluOpBus] ex_aluop,
	input wire[`RegBus] ex_mem_addr,
	input wire[`RegBus] ex_reg2,
	
	// load/store相关输出端口
	output reg[`AluOpBus] mem_aluop,
	output reg[`RegBus] mem_mem_addr,
	output reg[`RegBus] mem_reg2,
	
	// 流水线暂停
	input wire[5:0] stall
	
	// //转移指令相关
	// input wire[`RegBus] ex_hi,
    // input wire[`RegBus] ex_lo,
    // input wire ex_whilo, 
    // output reg[`RegBus] mem_hi,
    // output reg[`RegBus] mem_lo,
    // output reg mem_whilo

);

	always @ (posedge clk) begin
		if(rst == `RstEnable) begin
		  mem_wd <= `NOPRegAddr;
	      mem_wreg <= `WriteDisable;
		  mem_wdata <= `ZeroWord;
		  mem_aluop <= `EXE_NOP;
		  mem_mem_addr <=`ZeroWord;
		  mem_reg2 <= `ZeroWord;
		//   mem_hi <= `ZeroWord;
        //   mem_lo <= `ZeroWord;
        //   mem_whilo <= `WriteDisable;        
		  // 执行暂停，访存继续，那么传送空指令
		end else if(stall[3] == `Stop && stall[4] == `NoStop) begin
            mem_wd <= `NOPRegAddr;
            mem_wreg <= `WriteDisable;
            mem_wdata <= `ZeroWord;
            mem_aluop <= `EXE_NOP;
            mem_mem_addr <=`ZeroWord;
            mem_reg2 <= `ZeroWord;	
            
            // mem_hi <= `ZeroWord;
            // mem_lo <= `ZeroWord;
            // mem_whilo <= `WriteDisable;	
            // 正常流动
		end else if(stall[3] == `NoStop) begin
			mem_wd <= ex_wd;
			mem_wreg <= ex_wreg;
			mem_wdata <= ex_wdata;	
			
			mem_aluop <= ex_aluop;
			mem_mem_addr <= ex_mem_addr;
			mem_reg2 <= ex_reg2;
			
			// mem_hi <= ex_hi;
            // mem_lo <= ex_lo;
            // mem_whilo <= ex_whilo;    		
		end  
	end     
			

endmodule