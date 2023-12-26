`timescale 1ns / 1ps
`include "define.vh"

module ctrl(

	input wire rst,

	input wire stallreq_from_id,

    // 请求暂停流水线
	input wire stallreq_from_ex,
	output reg[5:0] stall       
	
);

    // 000000 -> PC，取指，译码，执行，访存，回写  0不停1停
	always @ (*) begin
		if(rst == `RstEnable) begin
			stall <= 6'b000000;
		end else if(stallreq_from_ex == `Stop) begin
		  // 取指译码执行暂停，访存回写继续
			stall <= 6'b001111;
		end else if(stallreq_from_id == `Stop) begin
		 // 取指译码暂停，执行访存回写继续
			stall <= 6'b000111;			
		end else begin
		// 正常流动
			stall <= 6'b000000;
		end    
	end     
			

endmodule
