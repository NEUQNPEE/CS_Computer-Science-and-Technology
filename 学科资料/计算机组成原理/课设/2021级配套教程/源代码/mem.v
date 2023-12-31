`timescale 1ns / 1ns
`include "define.vh"
module mem(
	input wire	rst,
	//来自执行阶段的信息	
	input wire[`RegAddrBus] wd_i,
	input wire wreg_i,
	input wire[`RegBus] wdata_i,
	
	//送到回写阶段的信息
	output reg[`RegAddrBus] wd_o,
	output reg wreg_o,
	output reg[`RegBus] wdata_o,
	
	// load/store 相关接口，mem_addr_i已经转化为有效地址，reg2_i存数
	input wire[`AluOpBus] aluop_i,
    input wire[`RegBus] mem_addr_i,
    input wire[`RegBus] reg2_i,
    
    // 来自 RAM 的数据
    input wire[`RegBus] mem_data_i,
    
    // 送到 RAM 的消息
    output reg[`RegBus]  mem_addr_o,
    // 1 写 0 读
    output wire  mem_we_o,
    // 字节选择信号
    output reg[3:0]  mem_sel_o,
    // 存入 RAM 的数据
    output reg[`RegBus]  mem_data_o,
    // 使能信号
    output reg mem_ce_o
    
    // //移动指令相关（数据相关）
	// input wire[`RegBus] hi_i,
    // input wire[`RegBus] lo_i,
    // input wire  whilo_i,   
	// output reg[`RegBus] hi_o,
    // output reg[`RegBus] lo_o,
    // output reg whilo_o
	
);

    wire[`RegBus] zero32;
    reg  mem_we;
    // RAM 读写信号
    assign mem_we_o = mem_we;
    assign zero32 = `ZeroWord;


	always @ (*) begin
		if(rst == `RstEnable) begin
			wd_o <= `NOPRegAddr;
			wreg_o <= `WriteDisable;
		    wdata_o <= `ZeroWord;
		    
		    mem_addr_o <= `ZeroWord;
            mem_we <= `WriteDisable;
            mem_sel_o <= 4'b0000;
            mem_data_o <= `ZeroWord;
            mem_ce_o <= `ChipDisable; 
            
            // hi_o <= `ZeroWord;
            // lo_o <= `ZeroWord;
            // whilo_o <= `WriteDisable;    
		end else begin
		    wd_o <= wd_i;
			wreg_o <= wreg_i;
			wdata_o <= wdata_i;
			
			mem_we <= `WriteDisable;
            mem_addr_o <= `ZeroWord;
            mem_sel_o <= 4'b1111;
            mem_ce_o <= `ChipDisable;   
            mem_data_o <=`ZeroWord;
            // hi_o <= hi_i;
            // lo_o <= lo_i;
            // whilo_o <= whilo_i;    
            case(aluop_i)
                `LW_OP:begin
                    mem_addr_o <= mem_addr_i;
                    // read
                    mem_we <= `WriteDisable;
                    wdata_o <= mem_data_i;
                    // 四个芯片各读出一个字节 （多体并行）
                    mem_sel_o <= 4'b1111;
                    mem_ce_o <= `ChipEnable;        
                end
                `SW_OP:begin
                    mem_addr_o <= mem_addr_i;
                    mem_we <= `WriteEnable;
                    mem_data_o <= reg2_i;
                    mem_sel_o <=4'b1111;
                    mem_ce_o <=`ChipEnable;
                end
                default: begin end
            endcase 
		end    //if
	end      //always
			

endmodule